FROM autostructure/puppet_tomcat:latest as builder

COPY manifests /manifests

COPY hiera.yaml /hiera.yaml
COPY hieradata /hieradata

RUN FACTER_hostname=some_image /opt/puppetlabs/bin/puppet apply manifests/init.pp --hiera_config=/hiera.yaml --detailed-exitcodes --verbose --show_diff --summarize  --app_management ; \
    rc=$?; if [ $rc -ne 0 ] && [ $rc -ne 2 ]; then exit 1; fi && \
    apt-get -y update -y && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

FROM tomcat:8.5-alpine

COPY --from=builder /usr/local/tomcat/conf    /usr/local/tomcat/conf
COPY --from=builder /usr/local/tomcat/webapps /usr/local/tomcat/webapps
COPY --from=builder /usr/local/tomcat/ext_lib/* /usr/local/tomcat/lib/
COPY --from=builder /usr/local/tomcat/bin/setenv.sh /usr/local/tomcat/bin/setenv.sh
