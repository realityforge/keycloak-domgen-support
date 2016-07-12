require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/custom_pom'

desc 'KeyCloak Domgen Support'
define 'keycloak-domgen-support' do
  project.group = 'org.realityforge.keycloak.domgen'
  compile.options.source = '1.7'
  compile.options.target = '1.7'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/keycloak-domgen-support')
  pom.add_developer('realityforge', 'Peter Donald', 'peter@realityforge.org', ['Developer'])

  compile.with :javaee_api, :jsr305_annotations, :keycloak_core, :keycloak_adapter_core, :keycloak_adapter_spi

  package(:jar)
  package(:sources)
  package(:javadoc)
end
