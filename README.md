# HX Events

![Java CI with Maven](https://github.com/chainhaus/hx-events-backend/workflows/Java%20CI%20with%20Maven/badge.svg)

## 1. Deployment
The application can be installed to run as a systemd service.  
See the documentation [here](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment-systemd-service) how to install a spring boot app as systemd service.
The service file (hx-events.service) is saved in the root folder of this repository.


## 2. Starting/Stopping the deployed application 
If it is installed as systemd service then it can be started/stopped using systemd commands

```shell script
sudo systemctl start hx-events.service
sudo systemctl stop hx-events.service
sudo systemctl restart hx-events.service
sudo systemctl status hx-events.service
```

Refer to man systemctl for more details.

## 3. Viewing logs
The application logs are saved in the home directory by default, and rotated daily.

If the application is running as systemd service then the logs can also be viewed using journalctl commands

```shell script
sudo journalctl -u hx-events.service
```

Refer to man journalctl for more details.

## 4. Property Encryption
Sensitive properties (like smtp credentials, application secrets) are saved in encrypted format (using [jasypt](https://github.com/ulisesbocchio/jasypt-spring-boot)) in this repository.

#### Encryption

To encrypt a single value run:

```shell script
mvn jasypt:encrypt-value -Djasypt.encryptor.password="the password" -Djasypt.plugin.value="theValueYouWantToEncrypt"
```

To encrypt placeholders in `src/main/resources/application.properties`, simply wrap any string with `DEC(...)`.
For example:

```properties
sensitive.password=DEC(secret value)
regular.property=example
```

Then run:

```shell script
mvn jasypt:encrypt -Djasypt.encryptor.password="the password"
```

Which would edit that file in place resulting in:

```properties
sensitive.password=ENC(encrypted)
regular.property=example
```

The file name and location can be customised.

#### Decryption

To decrypt a single value run:

```shell script
mvn jasypt:decrypt-value -Djasypt.encryptor.password="the password" -Djasypt.plugin.value="DbG1GppXOsFa2G69PnmADvQFI3esceEhJYbaEIKCcEO5C85JEqGAhfcjFMGnoRFf"
```

To decrypt placeholders in `src/main/resources/application.properties`, simply wrap any string with `ENC(...)`. For
example:

```properties
sensitive.password=ENC(encrypted)
regular.property=example
```

This can be decrypted as follows:

```shell script
mvn jasypt:decrypt -Djasypt.encryptor.password="the password"
```

Which would output the decrypted contents to the screen:

```properties
sensitive.password=DEC(decrypted)
regular.property=example
```

## 5. Additional server configs.
##### IP tables

```shell script
sudo iptables -t nat -A PREROUTING -p tcp --dport 443 -j REDIRECT --to-port 8443
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
```
