# Generated with Water Generator
# The Goal of feature test is to ensure the correct format of json responses
# If you want to perform functional test please refer to ApiTest
Feature: Check Role Rest Api Response

  Scenario: Role CRUD Operations

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url 'http://localhost:8080/water/roles'
    # ---- Add entity fields here -----
    And request { "name": "exampleField","description":"role"}
    # ---------------------------------
    When method POST
    Then status 200
    # ---- Matching required response json ----
    And match response ==
    """
      { "id": #number,
        "name":"exampleField",
        "entityVersion":1,
        "entityCreateDate":'#number',
        "entityModifyDate":'#number',
        "description": "role"
       }
    """
    * def entityId = response.id
    
    # --------------- UPDATE -----------------------------

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url 'http://localhost:8080/water/roles'
    # ---- Add entity fields here -----
    And request { "id":"#(entityId)","entityVersion":1,"name": "nameUpdated","description":"description"}
    # ---------------------------------
    When method PUT
    Then status 200
    # ---- Matching required response json ----
    And match response ==
    """
      { "id": #number,
        "entityVersion":2,
        "entityCreateDate":'#number',
        "entityModifyDate":'#number',
        "name": "nameUpdated",
        "description":"description"
       }
    """
  
  # --------------- FIND -----------------------------

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url 'http://localhost:8080/water/roles/'+entityId
    # ---------------------------------
    When method GET
    Then status 200
    # ---- Matching required response json ----
    And match response ==
    """
      { "id": #number,
        "entityVersion":2,
        "entityCreateDate":'#number',
        "entityModifyDate":'#number',
        "name": 'nameUpdated',
        "description":"description"
       }
    """
    
  # --------------- FIND ALL -----------------------------

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url 'http://localhost:8080/water/roles'
    When method GET
    Then status 200
    And match response ==
    """
      {
        "numPages":1,
        "currentPage":1,
        "nextPage":1,
        "delta":20,
        "results":[
           {
              "id":#number,
              "entityVersion":#number,
              "entityCreateDate":'#number',
              "entityModifyDate":'#number',
              "name":"roleManager",
              "description":""
           },
           {
              "id":#number,
              "entityVersion":#number,
              "entityCreateDate":'#number',
              "entityModifyDate":'#number',
              "name":"roleViewer",
              "description":""
           },
           {
              "id":#number,
              "entityVersion":#number,
              "entityCreateDate":'#number',
              "entityModifyDate":'#number',
              "name":"roleEditor",
              "description":""
           },
          {
            "id": #number,
            "entityVersion":2,
            "entityCreateDate":'#number',
            "entityModifyDate":'#number',
            "name": 'nameUpdated',
            "description":"description"
          }
        ]
      }
    """
  
  # --------------- DELETE -----------------------------

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url 'http://localhost:8080/water/roles/'+entityId
    When method DELETE
    # 204 because delete response is empty, so the status code is "no content" but is ok
    Then status 204
