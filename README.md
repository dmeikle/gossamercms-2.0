## Testing 
First, run the server. You can either run the test-server in the root api:
```aiignore
./run-server.sh
```
that will run the development database to check values against.
or you can run the test server:
```aiignore
./run-test-server.sh
```
Once you have an api server running, from the root module (gossamercms-2.0) run this following command.
Specify which individual module you want to run tests on:
```aiignore
./run-tests.sh users  
```
or auth|users|mvc||firebase|media|notifications|rbac