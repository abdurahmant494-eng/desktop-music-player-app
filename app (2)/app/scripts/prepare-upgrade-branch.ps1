# Run from the repository root (PowerShell)
# Usage: .\scripts\prepare-upgrade-branch.ps1 [-Push]
param([switch]$Push)

# Ensure git is available
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Error "git is not available on PATH. Install git and rerun."
    exit 2
}

# Ensure we are in a git repo
$root = (git rev-parse --show-toplevel) 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Error "Not in a git repository. Run this from the repo root."
    exit 3
}

# Ensure a clean working tree
$status = git status --porcelain
if ($status) {
    Write-Error "Working tree is not clean. Commit or stash changes first."
    git status --porcelain
    exit 4
}

$branch = 'upgrade/java-21'
Write-Host "Creating branch: $branch"
git checkout -b $branch

# Files to add (this script assumes only the upgrade changes are intended)
$files = @(
    'pom.xml',
    'README.md',
    'src/main/java/com/app/Track.java',
    'src/main/java/com/app/Playlist.java',
    'src/main/java/com/app/PlaybackManager.java'
)

foreach ($f in $files) {
    if (Test-Path $f) { git add $f } else { Write-Warning "$f not found; skipping" }
}

$commitMsg = "Upgrade to Java 21 and JavaFX 21; adapt Track, Playlist, PlaybackManager and tests"
git commit -m $commitMsg

# Export patch
$patchFile = Join-Path -Path $root -ChildPath "upgrade-java-21.patch"
git format-patch -1 HEAD --stdout > $patchFile
Write-Host "Patch exported to: $patchFile"

if ($Push) {
    Write-Host "Pushing branch to remote 'origin'"
    git push -u origin $branch
}

Write-Host "Done. Review the branch and push when ready."