## Creating an Endpoints Test File

The system only needs a json body. The "auth" element is for the controller to login
and get an authorization token. This token is attached to the request headers as a Bearer token.

```aiignore

{
  "auth": {
    "login": {
      "url": "/auth/login",
      "email": "test.user1@example.com",
      "password": "SuperSecurePass123!"
    }
  },
  "beforeEachSql": [

  ],
  "tests": [
    {
      "name": "start masquerade",
      "method": "POST",
      "url": "/admin/auth/start-masquerading/633ca6ad-ea38-49d3-8026-9721d3c409c6",
      "expectedStatus": 200,
      "ignoreFields": [
        "token",
        "refreshToken"
      ],
      "storeResponseFields": {
        "newToken": "token"
      },
      "expectedBody": {"userId":"633ca6ad-ea38-49d3-8026-9721d3c409c6","email":"test.user250@example.com","firstname":"Provider1","lastname":"Chen","contexts":[{"id":"cec411f8-4696-4ddb-b3b2-5a808d8be0af","userId":"633ca6ad-ea38-49d3-8026-9721d3c409c6","roleId":"6e8c1f3c-dc9f-470d-b203-83a43154cbac","contextType":"default","metadata":{"theme":"light","homepage":"/dashboard-pages/front-desk-dashboard","language":"en-US","timezone":"UTC","defaultContext":true,"newFeatureFlag":true,"organizationId":"2717da83-5036-408b-8dad-af70b5fe872a"},"createdAt":"2026-10-01T05:11:42.548652Z","default":false}]}
    }


  ]
}

```
