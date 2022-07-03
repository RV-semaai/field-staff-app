# field-staff-app
Field staff app is an app for field staff that does the customer acquisition and also sales
### Branch rules


1. **Development branch** The development branch is the default branch of the source repository. Any new addition in terms of features/bug-fixes should raise a pull request to the development branch.

2. **Release branch** During making a release to production environment, development branch will be checkout out to a new branch named release/version-name. The release/* branch can be created & pushed only by Maintainers. In the release branch, we will make a version commit & raise a pull request to the master branch.

3. **Create a Release Tag**- git tag -a vx.xx -m "change"; git push origin --tags
Live branch The master branch will contain the source code of the present/active release. Once a push is made to the master branch, we will release the code to the production environment.

4. **Feature development** For any new feature development, checkout the source from the default branch (development) & create your branch in form of the syntax: feature-feature-name

5. **Bug fixes / Enhancements** For any bug fixes or enhancements, checkout the source from the default branch (development) & create your branch in form of the syntax: enhancement-enhancement-name./bug-fix-bug-fix-name)
