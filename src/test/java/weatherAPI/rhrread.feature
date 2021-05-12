@rhrread
Feature: To test the api of current weather

  @rhrread_01
  Scenario Outline: response code 200
    Given I go to "<api>" with "<parameters>"
    And I check status code is 200
    And I check response type is "json"
    And I check json file is in the correct format
    And I measure response time within 500 milliseconds

    Examples: 
      | api     | parameters     |
      | rhrread | rhrread_normal |

  @rhrread_02
  Scenario Outline: response code 200 without language
    Given I go to "<api>" with "<parameters>"
    And I check status code is 200
    And I check response type is "json"
    And I check json file is in the correct format
    And I measure response time within 500 milliseconds

    Examples: 
      | api     | parameters      |
      | rhrread | rhrread_no_lang |
      
  @rhrread_03
  Scenario Outline: response code 200 with special character and spacing
    Given I go to "<api>" with "<parameters>"
    And I check status code is 200
    And I check response type is "json"
    And I check json file is in the correct format
    And I measure response time within 500 milliseconds
    
    Examples: 
      | api     | parameters                |
      | rhrread | rhrread_specialChar_space |
      
  @rhrread_04
  Scenario Outline: response code 200 with different letter casing
    Given I go to "<api>" with "<parameters>"
    And I check status code is 200
    And I check response type is "json"
    And I check json file is in the correct format
    And I measure response time within 500 milliseconds

    Examples: 
      | api     | parameters         |
      | rhrread | rhrread_cap_letter |
     
  @rhrread_05
  Scenario Outline: response code 301/307 for http request 
    Given I go to "<api>" with "<parameters>" without redirect
    And I check status code is 301 or 307
    And I check response type is "html"
    And I check "Moved Permanently/Temporary Redirect" is shown
    And I measure response time within 500 milliseconds
    And I go to "<api>" with "<parameters>" with redirect
   	And I check status code is 200
   	And I check response type is "json"
    And I check json file is in the correct format
    And I measure response time within 500 milliseconds

    Examples: 
      | api          | parameters         |
      | rhrread_http | rhrread_normal     |
      
      
  @rhrread_06
  Scenario Outline: super long url
    Given I go to "<api>" with "<parameters>" with long url
    And I check status code is 414
    And I check response type is "html"
    And I check "Request-URI Too Long" is shown
    And I measure response time within 500 milliseconds

    Examples: 
      | api          | parameters         |
      | rhrread      | rhrread_normal     |
      
  @rhrread_07
  Scenario Outline: invalid parameter
    Given I go to "<api>" with "<parameters>"
    And I check status code is 200
    And I check response type is "html"
    And I check "Please include valid parameters in API request" is shown
    And I measure response time within 500 milliseconds

    Examples: 
      | api          | parameters                    |
      | rhrread      | rhrread_incorrect_dataType    |
      | rhrread      | rhrread_incorrect_lang        |
      | rhrread      | rhrread_dataType_null         |
      
      @rhrread_08
  Scenario: performance test
    Given I carry out performance test
		Then I verify result file is generated
      