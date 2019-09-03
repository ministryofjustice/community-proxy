
### Example deploy command
```
helm --namespace court-probation-dev  --tiller-namespace court-probation-dev upgrade community-proxy ./community-proxy/ --install --values=values-dev.yaml 
```

### Rolling back a release
Find the revision number for the deployment you want to roll back:
```
helm --tiller-namespace court-probation-dev history community-proxy -o yaml
```
(note, each revision has a description which has the app version and circleci build URL)

Rollback
```
helm --tiller-namespace court-probation-dev rollback community-proxy [INSERT REVISION NUMBER HERE] --wait
```

### Helm init

```
helm init --tiller-namespace court-probation-dev --service-account tiller --history-max 200
```
