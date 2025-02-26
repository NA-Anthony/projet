#!/bin/bash

# Arrêter le script en cas d'erreur
set -e

path=$(pwd)

# Charger le fichier de configuration
config_file="config.properties"

if [ ! -f "$config_file" ]; then
    echo "Erreur : Le fichier de configuration '$config_file' est introuvable."
    exit 1
fi

# Lire la valeur de tomcat_path
tomcat_path=$(grep "^tomcat_path=" "$config_file" | cut -d '=' -f2)
projet=$(grep "^projet=" "$config_file" | cut -d '=' -f2)


if [ -z "$tomcat_path" ]; then
    echo "Sélectionnez le dossier Tomcat..."
    tomcat_path=$(osascript -e 'tell application "Finder" to choose folder with prompt "Sélectionnez le dossier Tomcat"' -e 'POSIX path of result')

    # Supprimer le dernier "/" s'il est présent
    tomcat_path=$(echo "$tomcat_path" | sed 's:/*$::')

    # Sauvegarde dans le fichier de configuration
    echo "$tomcat_path" >> "$config_file"
fi


# Définir les répertoires source, temporaire et de travail
framework_src="/Users/nakanyanthony/Documents/GitHub/projet/Framework"
src_dir="$path/src"
temp_src="$path/temp-src"
work_dir="$tomcat_path/webapps"
lib_dir="$path/lib"
config_dir="$path/config"
web_dir="$path/web"
tomcat_bin="$tomcat_path/bin"
destination_war="$tomcat_path/webapps/$projet.war"

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