package com.inclusive.adaptiveeducationservice.domain.adaptation.decision;

public interface PolicySelector {
    PolicyResult select(DecisionContext context);
}