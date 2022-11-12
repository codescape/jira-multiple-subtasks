# How to release a new version?

Releasing a new version helps to bring out new features and improvements to our customers.
To keep track of all changes we do the following:

1. update the [POM](pom.xml) file with the new version number (using [Calendar Versioning](https://calver.org) style `YY.0M.MICRO` since 20.05)
1. add the new version, a release title and the release date to the [Changelog](docs/changelog.md)
1. update the [Compatibility Matrix](docs/compatibility-matrix.md) with the new version number
1. create a tag for the new version with reference to the latest commit
    ```
    git rev-parse HEAD
    git tag -a <version> -m "<version>" <commit hash>
    git push origin <version>
    ```
1. upload and promote the new version at [Atlassian Marketplace](https://marketplace.atlassian.com/manage/plugins/de.codescape.jira.plugins.multiple-subtasks/versions)
1. add new version number to [Service Desk versions](https://codescape.atlassian.net/plugins/servlet/project-config/CASUP/administer-versions)
