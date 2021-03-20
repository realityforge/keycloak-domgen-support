require 'buildr/git_auto_version'
require 'buildr/gpg'

Buildr::MavenCentral.define_publish_tasks(:profile_name => 'org.realityforge', :username => 'realityforge')

desc 'KeyCloak Domgen Support'
define 'keycloak-domgen-support' do
  project.group = 'org.realityforge.keycloak.domgen'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'
  project.compile.options.warnings = true
  project.compile.options.other = %w(-Werror -Xmaxerrs 10000 -Xmaxwarns 10000)

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/keycloak-domgen-support')
  pom.add_developer('realityforge', 'Peter Donald')

  compile.with :javaee_api, :jsr305_annotations, :keycloak_core, :keycloak_adapter_core, :keycloak_adapter_spi

  package(:jar)
  package(:sources)
  package(:javadoc)

  ipr.add_component_from_artifact(:idea_codestyle)
  ipr.add_code_insight_settings
  ipr.add_nullable_manager
  ipr.add_javac_settings('-Xlint:all -Werror -Xmaxerrs 10000 -Xmaxwarns 10000')
end
