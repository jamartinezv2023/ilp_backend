# Renderer contract for ILP original candidate instruments

The renderer is presentation infrastructure, not a source of scientific equivalence. It must load exactly one immutable instrument version, preserve item order and identifiers, render only that instrument's response scale, and submit instrument ID, version, item ID, option value, timestamps and missingness without computing undocumented classifications.

## Required behavior

- Responsive, keyboard-operable and screen-reader-compatible presentation.
- One question or a small accessible group per view, with progress based on the selected instrument only.
- Save/resume bound to participant, administration, instrument and version.
- No silent substitution of items, scales, scoring or consent text.
- No display of low/medium/high bands, learning types, diagnoses or career prescriptions in candidate versions.
- Results show continuous dimensions, answered counts, missingness, provisional status and human-interpretation warning.
- Research preview and field administration are separate modes. Field mode remains disabled until ethics and release gates pass.