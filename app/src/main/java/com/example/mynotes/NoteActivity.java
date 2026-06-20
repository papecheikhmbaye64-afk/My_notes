package com.example.mynotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NoteActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private RelativeLayout root;
    private DatabaseHelper db;
    private int noteId = -1;
    private String colorHex = "828282";
    private int isFav = 0;
    private long dateCreated = 0;
    private boolean isDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        db = new DatabaseHelper(this);
        etTitle = findViewById(R.id.editTextTitle);
        etContent = findViewById(R.id.editTextContent);
        root = findViewById(R.id.noteActivityRoot);
        Button btnAction = findViewById(R.id.btnActionBottom);
        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView btnShare = findViewById(R.id.btnShare);
        ImageView btnDelete = findViewById(R.id.btnDelete);

        // Récupération de la préférence de thème pour l'affichage (optionnel mais recommandé)
        SharedPreferences prefs = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        isDarkMode = prefs.getBoolean("IsDark", false);

        // Récupération de la couleur de fond
        if (getIntent().hasExtra("COLOR")) {
            colorHex = getIntent().getStringExtra("COLOR");
            try {
                // Gestion du hashtag pour éviter les crashs si absent ou présent
                String safeColor = colorHex.startsWith("#") ? colorHex : "#" + colorHex;
                root.setBackgroundColor(Color.parseColor(safeColor));
            } catch (IllegalArgumentException e) {
                root.setBackgroundColor(isDarkMode ? Color.parseColor("#121212") : Color.parseColor("#FAFAFA"));
            }
        }

        // Mode Modification détecté
        if (getIntent().hasExtra("ID")) {
            noteId = getIntent().getIntExtra("ID", -1);
            etTitle.setText(getIntent().getStringExtra("TITLE"));
            etContent.setText(getIntent().getStringExtra("CONTENT"));
            isFav = getIntent().getIntExtra("FAV", 0);

            // Correction ici : la clé envoyée par MainActivity est "CREATED" et non "DATE_CREATED"
            dateCreated = getIntent().getLongExtra("CREATED", System.currentTimeMillis());

            btnAction.setText("Modifier");
            btnDelete.setVisibility(View.VISIBLE); // Afficher le bouton supprimer en mode édition
        } else {
            // Mode Création
            btnAction.setText("Créer");
            btnDelete.setVisibility(View.GONE); // Cacher le bouton supprimer pour une nouvelle note
            dateCreated = System.currentTimeMillis();
        }

        // Gestion du clic sur le bouton Retour
        btnBack.setOnClickListener(v -> finish());

        // Gestion du clic sur le bouton Partager
        btnShare.setOnClickListener(v -> shareNote());

        // Gestion du clic sur le bouton Supprimer
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        // Gestion du clic sur le bouton d'action principale (Créer / Modifier)
        btnAction.setOnClickListener(v -> saveOrUpdateNote());
    }

    private void saveOrUpdateNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        // Le titre ne doit pas être vide selon les critères d'UX d'une note
        if (title.isEmpty()) {
            Toast.makeText(this, "Le titre ne peut pas être vide", Toast.LENGTH_SHORT).show();
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (noteId == -1) {
            // MODE CRÉATION : ID temporaire à 0, géré par l'autoincrement de SQLite
            Note newNote = new Note(0, title, content, colorHex, dateCreated, currentTime, 0);

            // Exécution réelle de l'ajout en BDD
            db.addNote(newNote);

            Toast.makeText(this, "Note créée avec succès", Toast.LENGTH_SHORT).show();
        } else {
            // MODE MODIFICATION : On conserve l'ID et la date de création originale
            Note updatedNote = new Note(noteId, title, content, colorHex, dateCreated, currentTime, isFav);

            // Exécution réelle de la mise à jour en BDD
            db.updateNote(updatedNote);

            Toast.makeText(this, "Note mise à jour", Toast.LENGTH_SHORT).show();
        }

        finish(); // Retourner à l'écran principal (la liste se rechargera dans onResume)
    }

    private void shareNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        String shareBody = title + "\n\n" + content;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent, "Partager via"));
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la note")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette note ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {

                    // Exécution réelle de la suppression en BDD
                    db.deleteNote(noteId);

                    Toast.makeText(NoteActivity.this, "Note supprimée", Toast.LENGTH_SHORT).show();
                    finish(); // Retour à la liste après suppression
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}