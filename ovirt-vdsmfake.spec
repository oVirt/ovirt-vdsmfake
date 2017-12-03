Name:       ovirt-vdsmfake
Version:    %{?_version}
Release:    %{?_release}%{?dist}
Summary:    A vdsm simulator, simulating multi vdsm nodes with VMs, discs etc.

License:    ASL 2.0
URL:        http://www.ovirt.org
Source0:    %{name}-%{version}-%{_release}.tar.gz

%description
A vdsm simulator, simulating multi vdsm nodes with VMs, discs etc.

%global cacheDir %{_localstatedir}/cache/%{name}
%global javaDir  %{_javadir}/%{name}
%global logDir   %{_var}/log/%{name}

%prep
%setup -c

%build
mvn clean package -DskipTests

%install
mkdir -p %{buildroot}%{javaDir}
mkdir -p %{buildroot}%{cacheDir}
mkdir -p %{buildroot}%{logDir}
mkdir -p %{buildroot}%{_bindir}
install -p -m 644 target/vdsmfake-swarm.jar %{buildroot}%{javaDir}
echo -e "#!/bin/sh \n java -jar %{javaDir}/vdsmfake-swarm.jar \
    -Dswarm.logging.file-handlers.FILE.file.path=%{logDir}/vdsmfake.log \
    -DcacheDir=%{cacheDir} \
    -Dfake.host=0.0.0.0 \
    -DPS1=_" > %{buildroot}%{_bindir}/vdsmfake
chmod 755 %{buildroot}%{_bindir}/vdsmfake

%define debug_package %{nil}

%files
%dir %{cacheDir}
%dir %{logDir}
%{javaDir}/vdsmfake-swarm.jar
%{_bindir}/vdsmfake

%changelog
