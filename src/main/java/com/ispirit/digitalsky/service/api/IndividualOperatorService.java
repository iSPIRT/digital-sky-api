package com.ispirit.digitalsky.service.api;


import com.ispirit.digitalsky.domain.IndividualOperator;

public interface IndividualOperatorService {

    IndividualOperator createNewOperator(IndividualOperator individualOperator);

    IndividualOperator updateOperator(long id, IndividualOperator individualOperator);

    IndividualOperator find(long id);
}
