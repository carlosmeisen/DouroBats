# Custom Fonts Directory

Place your font files here to use custom typography in the app.

## Recommended Fonts for Material Design

Based on the Material Theme Builder output, the recommended font is **Montserrat**.

### Where to Download Fonts

1. **Google Fonts**: https://fonts.google.com/specimen/Montserrat
   - Click "Download family"
   - Extract the ZIP file

2. **Font Files Needed**:
   - `montserrat_regular.ttf` (Regular/400 weight)
   - `montserrat_medium.ttf` (Medium/500 weight)
   - `montserrat_semibold.ttf` (SemiBold/600 weight)
   - `montserrat_bold.ttf` (Bold/700 weight)

### How to Add Fonts

1. Download Montserrat from Google Fonts
2. Find the TTF files in the `static/` folder of the downloaded ZIP
3. Rename and copy them to this directory:
   ```
   core/ui/src/commonMain/composeResources/font/
   ├── montserrat_regular.ttf
   ├── montserrat_medium.ttf
   ├── montserrat_semibold.ttf
   └── montserrat_bold.ttf
   ```

4. The fonts will automatically be loaded by the Type.kt file

### File Naming Convention

- Use lowercase
- Use underscores instead of spaces or hyphens
- Format: `{fontname}_{weight}.ttf`
- Examples:
  - `montserrat_regular.ttf`
  - `montserrat_bold.ttf`
  - `roboto_italic.ttf`

### Supported Formats

- `.ttf` (TrueType Font) - Recommended
- `.otf` (OpenType Font)

Both work with Compose Multiplatform Resources.

## Current Setup

The app currently uses `FontFamily.Default` (system fonts).
Once you add font files here, update will be automatic.
