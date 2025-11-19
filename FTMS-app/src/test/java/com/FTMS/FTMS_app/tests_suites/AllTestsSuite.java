package com.FTMS.FTMS_app.tests_suites;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        Test1_EntityRepository.class,
        Test2_ComputingServices.class,
        Test3_WorkflowServices.class
})
public class AllTestsSuite {
}