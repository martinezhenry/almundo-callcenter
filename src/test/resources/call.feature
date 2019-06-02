Feature: the version can be retrieved
Scenario: client makes call to GET /call
  When the client calls /call
  Then the client receives status code of 200
  And the client receives server version 1.0