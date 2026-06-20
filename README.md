# 📒 MyNotes — Description & Guide d'utilisation

## C'est quoi ce projet ?

MyNotes est une application Android de prise de notes simple et colorée. Elle permet de créer, modifier, organiser et supprimer des notes, avec des couleurs personnalisées, un système de favoris, une recherche et un tri. Toutes les notes sont sauvegardées localement sur l'appareil via une base de données SQLite.

---

## 🗂️ Structure du projet

```
Mynotes/
└── app/src/main/
    ├── java/com/example/mynotes/
    │   ├── MainActivity.java       → Écran principal (liste des notes)
    │   ├── NoteActivity.java       → Écran de création / modification d'une note
    │   ├── NoteAdapter.java        → Gestion de l'affichage de chaque note dans la liste
    │   ├── DatabaseHelper.java     → Gestion de la base de données SQLite
    │   └── Note.java               → Modèle d'une note (titre, contenu, couleur, date...)
    └── res/
        ├── layout/
        │   ├── activity_main.xml   → Interface de la liste
        │   ├── activity_note.xml   → Interface d'une note
        │   └── item_note.xml       → Apparence d'une note dans la liste
        └── values/
            └── colors.xml          → Couleurs de l'application
```

---

## ✨ Fonctionnalités

| Fonctionnalité | Comment faire |
|---|---|
| Créer une note | Appuyer sur le bouton **+** en bas à droite |
| Choisir une couleur | Un panneau de couleurs apparaît après avoir appuyé sur **+** |
| Ouvrir / modifier une note | **Appui simple** sur la note |
| Ajouter / retirer des favoris | **Double-appui** sur la note |
| Supprimer par glissement | **Glisser** la note vers la gauche ou la droite |
| Supprimer par appui long | **Appui long** sur la note → dialog de confirmation |
| Supprimer depuis la note ouverte | Bouton 🗑️ en haut à droite (visible seulement en mode modification) |
| Rechercher une note | Barre de recherche en haut (filtre par titre) |
| Trier les notes | Bouton **Trier** → choisir le critère |
| Filtrer les favoris | Bouton **Favoris** (étoile) → affiche uniquement les favoris |
| Changer le thème | Icône lune / soleil en haut → bascule clair / sombre |
| Partager une note | Bouton partager ↗ dans la note ouverte |

---

## 📝 Créer une note

1. Sur l'écran principal, appuyez sur le bouton **+** (en bas à droite).
2. Un panneau de couleurs apparaît — choisissez une couleur pour votre note.
3. L'écran de création s'ouvre. Remplissez le **titre** et le **contenu**.
4. Appuyez sur **Créer** pour sauvegarder.

> ⚠️ Le titre est obligatoire. La note ne sera pas sauvegardée sans titre.

---

## ✏️ Modifier une note

1. Sur l'écran principal, faites un **appui simple** sur la note.
2. Modifiez le titre ou le contenu.
3. Appuyez sur **Modifier** pour enregistrer les changements.

---

## 🗑️ Supprimer une note

Il existe **trois façons** de supprimer une note :

### Méthode 1 — Glissement (swipe) ⭐ recommandé
- Sur la liste, **glissez la note vers la gauche ou vers la droite**.
- Un fond rouge avec une icône corbeille apparaît pendant le glissement.
- La note disparaît immédiatement.
- Un message **"Annuler"** s'affiche en bas pendant quelques secondes — appuyez dessus pour restaurer la note si c'était une erreur.

### Méthode 2 — Appui long
- Faites un **appui long** sur la note dans la liste.
- Une boîte de dialogue s'affiche : confirmez en appuyant sur **Supprimer**.

### Méthode 3 — Depuis la note ouverte
- Ouvrez la note avec un appui simple.
- Appuyez sur l'icône **corbeille** 🗑️ en haut à droite.
- Confirmez la suppression dans le dialog.

---

## ⭐ Gérer les favoris

- **Ajouter aux favoris** : double-appui sur une note → une étoile ★ apparaît sur la note.
- **Retirer des favoris** : double-appui à nouveau sur la même note.
- **Filtrer les favoris** : appuyez sur le bouton favoris en haut → seules les notes favorites s'affichent. Rappuyez pour tout réafficher.

---

## 🔍 Rechercher une note

- Tapez dans la **barre de recherche** en haut de l'écran.
- La liste se filtre en temps réel selon le **titre** de la note.
- Si aucune note ne correspond, le message *"Aucune note trouvée"* s'affiche.

---

## 📊 Trier les notes

Appuyez sur le bouton **Trier** pour choisir parmi :

- **Date de modification** (par défaut) — les plus récemment modifiées en premier
- **Date de création** — les plus récemment créées en premier
- **Alphabétique A → Z**
- **Alphabétique Z → A**
- **Favorites en premier**

---

## 🌙 Thème clair / sombre

- Appuyez sur l'icône en haut à droite (lune ou soleil) pour basculer entre le thème **clair** et **sombre**.
- Le thème est mémorisé entre les sessions.

---

## 📤 Partager une note

1. Ouvrez la note (appui simple).
2. Appuyez sur l'icône **partager** ↗ en haut.
3. Choisissez l'application via laquelle partager (WhatsApp, email, SMS...).

---

## 💾 Stockage des données

Toutes les notes sont stockées **localement sur l'appareil** dans une base SQLite. Aucune connexion internet n'est nécessaire. Les données ne sont pas sauvegardées dans le cloud.

---

## 🚀 Lancer le projet dans Android Studio

1. Décompresser le fichier `Mynotes.zip`.
2. Ouvrir **Android Studio** → `File` → `Open` → sélectionner le dossier `Mynotes`.
3. Attendre la synchronisation Gradle.
4. Connecter un appareil Android ou lancer un émulateur.
5. Appuyer sur **Run ▶**.
