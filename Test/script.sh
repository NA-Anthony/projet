#!/bin/bash

# Arrêter le script en cas d'erreur
set -e

# Définir les répertoires source, temporaire et de travail
framework_src="/Users/nakanyanthony/Documents/GitHub/projet/Framework"
src_dir="/Users/nakanyanthony/Documents/GitHub/projet/Test/src"
temp_src="/Users/nakanyanthony/Documents/GitHub/projet/Test/temp-src"
work_dir="/Users/nakanyanthony/Downloads/apache-tomcat-9.0.96/webapps"
lib_dir="/Users/nakanyanthony/Documents/GitHub/projet/Test/lib"
config_dir="/Users/nakanyanthony/Documents/GitHub/projet/Test/config"
web_dir="/Users/nakanyanthony/Documents/GitHub/projet/Test/web"
tomcat_bin="/Users/nakanyanthony/Downloads/apache-tomcat-9.0.96/bin"
destination_war="/Users/nakanyanthony/Downloads/apache-tomcat-9.0.96/webapps/destination.war"

cd "$framework_src"
./script.sh

echo "                              -------         Debut du déploiement         -------"

# Créer le répertoire temporaire
mkdir -p "$temp_src"
cd "$src_dir"

find "$src_dir" -name "*.java" -exec cp {} "$temp_src" \;

cd "$temp_src"

mkdir -p "$temp_src/WEB-INF/lib"

# Copier les fichiers de la bibliothèque dans le dossier temporaire
rsync -av --progress "$lib_dir/" "$temp_src/WEB-INF/lib/"

# Copier les fichiers de configuration dans le dossier temporaire
rsync -av --progress "$config_dir/" "$temp_src/WEB-INF/"

# Copier les fichiers de configuration dans le dossier temporaire
rsync -av --progress "$web_dir/" "$temp_src/"

classpath=$(find "$lib_dir" -name "*.jar" | tr '\n' ':')
javac -d "$temp_src/WEB-INF/classes/" -cp "$classpath" *.java

# Supprimer les fichiers .java dans le répertoire webapps
find "$temp_src" -name "*.java" -exec rm -f {} \;

jar cvf "$destination_war" *

rm -rf "$temp_src"

# Démarrage de Tomcat
cd "$tomcat_bin"
./startup.sh

echo "                          -------           Déploiement terminé          -------"