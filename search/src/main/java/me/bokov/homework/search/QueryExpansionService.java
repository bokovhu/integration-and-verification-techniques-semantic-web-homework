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

    private Set<String> ontologyBaseIRIs = new HashSet<> ();

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
        this.ontologyBaseIRIs = this.reasoner.getSubClasses (
                this.owlDataFactory.getOWLThing ()
        ).entities ()
                .map (e -> e.getIRI ().getNamespace ())
                .collect (Collectors.toSet ());

        System.out.println ("Loaded ontology, IRIs: " + String.join (", ", this.ontologyBaseIRIs));

    }

    public void init () {

        if (Options.getInstance ().getQueryExpansionOntologyFile () != null) {
            initOntology ();
        }

    }

    private Set<String> findRelevantTermsForWord (String word) {

        Set<String> result = new HashSet<> ();

        final Set<OWLAnnotationProperty> lookForAnnotations = Set.of (
                this.owlDataFactory.getRDFSLabel (),
                this.owlDataFactory.getRDFSComment ()
        );

        Stream.concat (
                this.ontologyBaseIRIs.stream ()
                        .map (iri -> this.owlDataFactory.getOWLClass (IRI.create (iri, word))),
                this.ontologyBaseIRIs.stream ()
                        .map (iri -> this.owlDataFactory.getOWLClass (IRI.create (iri, word)))
                        .flatMap (c -> this.reasoner.getSubClasses (c).entities ())
        )
                .distinct ()
                .filter (owlClass -> !owlClass.isBuiltIn ())
                .flatMap (
                        subClass -> Stream.concat (
                                EntitySearcher.getAnnotations (subClass, ontology)
                                        .filter (a -> lookForAnnotations.contains (a.getProperty ()))
                                        .map (a -> a.getValue ().toString ().strip ())
                                        .filter (s -> !s.contains (" ")),
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
