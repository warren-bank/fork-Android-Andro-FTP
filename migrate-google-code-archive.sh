#!/usr/bin/env bash

# https://sourceforge.net/projects/win32svn/files/1.8.17/apache24/svn-win32-1.8.17-ap24.zip/download
PATH="${PATH}:/c/PortableApps/win32svn/1.8.17-apache24/bin"

PROJECT='andro-ftp'
SVN_DIR="${PROJECT}-svn"
GIT_DIR="${PROJECT}-git"

# download svn dump
wget --no-check-certificate -nv "https://storage.googleapis.com/google-code-archive-source/v2/code.google.com/${PROJECT}/repo.svndump.gz"

# make local svn repo
svnadmin create "${SVN_DIR}"
zcat repo.svndump.gz|svnadmin load "${SVN_DIR}"

# make local git repo and migrate from svn
git svn init "file://$(pwd)/${SVN_DIR}" --stdlayout --no-metadata "${GIT_DIR}"
cd "${GIT_DIR}"
git svn fetch --all

# remove the last 2 commits, which (collectively) delete every file in the repo
git reset --hard HEAD~2
