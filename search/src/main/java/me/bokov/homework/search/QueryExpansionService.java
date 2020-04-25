package me.bokov.homework.search;

import me.bokov.homework.common.TextUtils;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class QueryExpansionService {

    private static final QueryExpansionService INSTANCE = new QueryExpansionService ();

    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private OWLDataFactory owlDataFactory;
    private OWLReasonerFactory owlReasonerFactory;
    private OWLOntologyManager owlOntologyManager;

    private String ontologyBaseIRI = null;

    private QueryExpansionService () {

    }

    public static QueryExpansionService getInstance () {
        return INSTANCE;
    }

    private void initOntology () {

        this.owlOntologyManager = OWLManager.createOWLOntologyManager ();

        try {

            this.ontology = this.owlOntologyManager.loadOntologyFromOntologyDocument (
                    Options.getInstance ().getQueryExpansionOntologyFile ()
            );

        } catch (Exception e) {
            throw new RuntimeException (e);
        }

        this.owlReasonerFactory = new ReasonerFactory ();
        this.reasoner = this.owlReasonerFactory.createReasoner (this.ontology);

        if (!this.reasoner.isConsistent ()) {
            throw new IllegalStateException ("Query expansion ontology is not consistent!");
        }

        this.owlDataFactory = this.owlOntologyManager.getOWLDataFactory ();
        this.ontologyBaseIRI = this.reasoner.getSubClasses (
                this.owlDataFactory.getOWLThing ()
        ).entities ()
                .findFirst ().map (owlClass -> owlClass.getIRI ().getNamespace ())
                .orElseThrow (() -> new IllegalStateException ("Could not determine ontology IRI namespace!"));

        System.out.println ("Loaded ontology, IRI: " + this.ontologyBaseIRI);

    }

    public void init () {

        if (Options.getInstance ().getQueryExpansionOntologyFile () != null) {
            initOntology ();
        }

    }

    private Set<String> findRelevantTermsForWord (String word) {

        Set<String> result = new HashSet<> ();

        final OWLClass foundClass = this.owlDataFactory.getOWLClass (
                IRI.create (this.ontologyBaseIRI, word)
        );
        final Set<OWLAnnotationProperty> lookForAnnotations = Set.of (
                this.owlDataFactory.getRDFSLabel (),
                this.owlDataFactory.getRDFSComment ()
        );

        Stream.concat (
                Stream.of (foundClass),
                this.reasoner.getSubClasses (foundClass).entities ()
        )
                .filter (owlClass -> !owlClass.isBuiltIn ())
                .flatMap (
                        subClass -> Stream.concat (
                                EntitySearcher.getAnnotations (subClass, ontology)
                                        .filter (a -> lookForAnnotations.contains (a.getProperty ()))
                                        .map (a -> a.getValue ().toString ()),
                                Stream.of (subClass.getIRI ().getRemainder ().orElse (null))
                        )
                ).filter (Objects::nonNull)
                .distinct ()
                .forEach (result::add);

        return result;

    }

    private Set<String> findRelevantTerms (Set<String> terms) {

        return terms.stream ()
                .flatMap (term -> findRelevantTermsForWord (term).stream ())
                .collect (Collectors.toSet ());

    }

    public Set<String> expandSearchTerms (Set<String> initialSearchTerms) {

        Set<String> expanded = new HashSet<> (initialSearchTerms);

        if (Options.getInstance ().getQueryExpansionOntologyFile () != null) {
            expanded.addAll (findRelevantTerms (initialSearchTerms));
        }

        return expanded.stream ()
                .map (TextUtils::tokenToWord)
                .collect (Collectors.toSet ());

    }

}
