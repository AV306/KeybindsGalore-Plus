#FROM gitpod/workspace-full-vnc
FROM gitpod/workspace-full

SHELL ["/bin/bash", "-c"]
#RUN sudo apt update && sudo apt upgrade -y && sudo apt autoremove #&& sudo apt install qt5-default -y
RUN source "/home/gitpod/.sdkman/bin/sdkman-init.sh" && sdk install java 21.0.6-zulu < /dev/null

#RUN source /etc/lsb-release
