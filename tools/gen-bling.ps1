# Generates every Bling texture and JSON file: jewelry items, the watch's clock faces, the 3D jewelry textures,
# the jeweler's bench (block, screen, recipe), the empty-slot icons, and names.
# Usage: .\tools\gen-bling.ps1
# Pixel art is drawn from the text maps below: one character per pixel, '.' is see-through.
$ErrorActionPreference = 'Stop'
trap { Write-Host "Error: $_"; exit 1 }
Add-Type -AssemblyName System.Drawing

$root = Join-Path $PSScriptRoot '..\src\main\resources'
$assets = Join-Path $root 'assets\bling'
$data = Join-Path $root 'data\bling'
$tex = Join-Path $assets 'textures'
$utf8 = New-Object System.Text.UTF8Encoding($false)

function Write-Json($path, $text) {
	$dir = Split-Path $path
	if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir | Out-Null }
	[System.IO.File]::WriteAllText($path, ($text.Trim() -replace "`r`n", "`n") + "`n", $utf8)
}

function Save-Bitmap($bmp, $path) {
	$dir = Split-Path $path
	if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir | Out-Null }
	$bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
	$bmp.Dispose()
}

function Color($hex) {
	if ($hex.Length -eq 6) { $hex = 'ff' + $hex }
	return [System.Drawing.Color]::FromArgb([Convert]::ToInt32($hex, 16))
}

# Turns a text map into a list of rows (an array of strings), so maps can be edited pixel by pixel.
function Rows($map) { return , [string[]]($map.Trim("`r", "`n") -split "`r?`n") }

# $palette maps a character to 'rrggbb' or 'aarrggbb'. $scale blows each pixel up (for the mod icon).
function Draw($rows, $palette, $path, $scale = 1) {
	$h = $rows.Count; $w = $rows[0].Length
	$bmp = New-Object System.Drawing.Bitmap ($w * $scale), ($h * $scale), ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
	for ($y = 0; $y -lt $h; $y++) {
		for ($x = 0; $x -lt $w; $x++) {
			$ch = [string]$rows[$y][$x]
			if ($ch -eq '.' -or -not $palette.ContainsKey($ch)) { continue }
			$c = Color $palette[$ch]
			for ($dy = 0; $dy -lt $scale; $dy++) { for ($dx = 0; $dx -lt $scale; $dx++) { $bmp.SetPixel($x * $scale + $dx, $y * $scale + $dy, $c) } }
		}
	}
	Save-Bitmap $bmp $path
}

function Set-Pixel($rows, $x, $y, $ch) {
	$row = $rows[$y].ToCharArray(); $row[$x] = $ch; $rows[$y] = -join $row
}

# ---- Materials ----
# id, display name, dark, mid, light
$metals = @(
	@('iron', 'Iron', '5e5e5e', 'b4b4b4', 'efefef'),
	@('gold', 'Gold', 'b0781c', 'f2c53d', 'fff2a6'),
	@('copper', 'Copper', '9a4a2c', 'e07a4f', 'f7b89a'),
	@('netherite', 'Netherite', '2a2427', '4d4447', '7d7176')
)
$gems = @(
	@('diamond', 'Diamond', '1d9e97', '4ee6dc', 'd7fff9'),
	@('emerald', 'Emerald', '0a7a33', '2fd36b', 'b3ffcf'),
	@('amethyst', 'Amethyst', '5b3596', '9f73e0', 'e2cbff')
)
# id, name used in item names, name on the bench, gem use (optional / required / none / iced, which is always diamonds)
$pieces = @(
	@('earrings', 'Earrings', 'Earrings', 'optional'),
	@('studs', 'Stud Earrings', 'Stud Earrings', 'required'),
	@('chain', 'Chain', 'Chain', 'optional'),
	@('iced_chain', 'Chain', 'Iced Out Chain', 'iced'),
	@('bracelet', 'Bracelet', 'Bracelet', 'optional'),
	@('watch', 'Watch', 'Watch', 'none'),
	@('iced_watch', 'Watch', 'Iced Out Watch', 'iced')
)
$diamond = $gems[0]

# ---- Item pictures ----
# d/m/l are the metal's dark/mid/light; x/o/H the gem's dark/mid/light (hashtable keys ignore case, so no G/g). Gem pixels are left out for plain pieces.
$art = @{}
$art['earrings'] = @'
................
................
....l......l....
....m......m....
...lmm....lmm...
..l...d..l...d..
..m...d..m...d..
..m...d..m...d..
..m...d..m...d..
...d.d....d.d...
....d......d....
....H......H....
...Hox....Hox...
....x......x....
................
................
'@
$art['chain'] = @'
................
.l............l.
.m............m.
.d............d.
..l..........l..
..m..........m..
...d........d...
...l........l...
....m......m....
.....d....d.....
......lmml......
.......Ho.......
......Hoox......
.......ox.......
................
................
'@
$art['bracelet'] = @'
................
................
................
....llmmmmmd....
..lm........md..
.lm..........md.
.m............d.
.m............d.
.dm..........dd.
..dm...Ho...dd..
....ddHooxdd....
.......ox.......
................
................
................
................
'@
$art['studs'] = @'
................
................
................
................
................
................
..mHHm....mHHm..
..Hoox....Hoox..
..ooxx....ooxx..
..mxxm....mxxm..
................
................
................
................
................
................
'@
$art['iced_chain'] = @'
................
.lH..........Hl.
.mo..........om.
.dH..........Hd.
..lo........ol..
..mH........Hm..
...do......od...
...lH......Hl...
....mo....om....
.....dHooHd.....
......xHHx......
.....xHooox.....
.....xoooox.....
......xoox......
.......xx.......
................
'@
# The plain versions: gem pixels become metal (the chain gets a small metal clasp instead).
$plain = @{}
$plain['earrings'] = (Rows $art['earrings'])[0..10] + @('................') * 5
$plain['chain'] = (Rows $art['chain'])[0..10] + @('.......md.......') + @('................') * 4
$plain['bracelet'] = Rows $art['bracelet']
$plain['bracelet'][9] = $plain['bracelet'][9] -replace '[Hox]', '.'
$plain['bracelet'][10] = $plain['bracelet'][10] -replace '[Hox]', 'd'
$plain['bracelet'][11] = '................'

$watch = @'
.....dmmmmd.....
.....dmmmmd.....
.....dmmmmd.....
....llllllll....
...lFFFFFFFFd...
...lFFFFFFFFd...
...lFFFFFFFFd...
...lFFFFFFFFdm..
...lFFFFFFFFdm..
...lFFFFFFFFd...
...lFFFFFFFFd...
...lFFFFFFFFd...
....dddddddd....
.....dmmmmd.....
.....dmmmmd.....
.....dmmmmd.....
'@
# The iced out watch: the same watch with a diamond case.
$icedWatch = Rows $watch
$icedWatch[3] = '....HoHoHoHo....'
$icedWatch[12] = '....oxoxoxox....'
for ($y = 4; $y -le 11; $y++) {
	Set-Pixel $icedWatch 3 $y $(if ($y % 2) { 'o' } else { 'H' })
	Set-Pixel $icedWatch 12 $y $(if ($y % 2) { 'x' } else { 'o' })
}
$watchBases = @{ 'watch' = (Rows $watch); 'iced_watch' = $icedWatch }

# The watch face, with its hour hand pointing at one of 12 hours (0 is 12 o'clock).
function Watch-Face($base, $hour) {
	$rows = [string[]]$base.Clone()
	Set-Pixel $rows 7 4 'S'; Set-Pixel $rows 8 4 'S'
	foreach ($p in @(@(7, 7), @(8, 7), @(7, 8), @(8, 8))) { Set-Pixel $rows $p[0] $p[1] 'K' }
	$angle = $hour * [Math]::PI / 6
	foreach ($r in 1.6, 2.4, 3.2) {
		$x = [int][Math]::Floor(8 + [Math]::Sin($angle) * $r)
		$y = [int][Math]::Floor(8 - [Math]::Cos($angle) * $r)
		if ($x -ge 4 -and $x -le 11 -and $y -ge 4 -and $y -le 11) { Set-Pixel $rows $x $y 'K' }
	}
	return , $rows
}

$lang = [ordered]@{ 'creativeTab.bling' = 'Bling' }
$itemIds = @()

foreach ($metal in $metals) {
	$mid, $mname, $md, $mm, $ml = $metal
	foreach ($piece in $pieces) {
		$pieceId, $pname, $null, $gemUse = $piece
		$variants = @(, $null)
		if ($gemUse -eq 'optional') { $variants += $gems }
		if ($gemUse -eq 'required') { $variants = $gems }
		foreach ($gem in $variants) {
			if ($gem) {
				$gid, $gname, $gd, $gm, $gl = $gem
				$id = "${mid}_${gid}_$pieceId"; $name = "$mname $gname $pname"
			} else {
				$id = "${mid}_$pieceId"; $name = "$mname $pname"
			}
			if ($gemUse -eq 'iced') { $name = "Iced Out $mname $pname" }
			$itemIds += $id
			$lang["item.bling.$id"] = $name
			$palette = @{ 'd' = $md; 'm' = $mm; 'l' = $ml; 'F' = 'f4f1e6'; 'K' = '2a2a2a'; 'S' = '9c9a90' }
			$shine = if ($gemUse -eq 'iced') { $diamond } else { $gem }
			if ($shine) { $palette['x'] = $shine[2]; $palette['o'] = $shine[3]; $palette['H'] = $shine[4] }

			if ($watchBases.ContainsKey($pieceId)) {
				# Like a clock, the watch's picture shows the time: a range of models picked by the time of day.
				# "daytime" counts from noon, so 0 hours on this scale is 12 o'clock.
				$entries = @()
				for ($h = 0; $h -lt 12; $h++) {
					$frame = "${id}_" + $h.ToString('00')
					Draw (Watch-Face $watchBases[$pieceId] $h) $palette (Join-Path $tex "item\$frame.png")
					Write-Json (Join-Path $assets "models\item\$frame.json") "{ `"parent`": `"minecraft:item/generated`", `"textures`": { `"layer0`": `"bling:item/$frame`" } }"
				}
				$entries += "{ `"threshold`": 0.0, `"model`": { `"type`": `"minecraft:model`", `"model`": `"bling:item/${id}_00`" } }"
				for ($j = 1; $j -le 24; $j++) {
					$frame = "${id}_" + ($j % 12).ToString('00')
					$threshold = ($j - 0.5).ToString([System.Globalization.CultureInfo]::InvariantCulture)
					$entries += "{ `"threshold`": $threshold, `"model`": { `"type`": `"minecraft:model`", `"model`": `"bling:item/$frame`" } }"
				}
				$spin = ($entries -join ",`n`t`t`t`t")
				Write-Json (Join-Path $assets "items\$id.json") @"
{
	"model": {
		"type": "minecraft:select",
		"property": "minecraft:context_dimension",
		"cases": [
			{
				"when": "minecraft:overworld",
				"model": {
					"type": "minecraft:range_dispatch",
					"property": "minecraft:time",
					"source": "daytime",
					"wobble": false,
					"scale": 24.0,
					"entries": [
				$spin
					]
				}
			}
		],
		"fallback": {
			"type": "minecraft:range_dispatch",
			"property": "minecraft:time",
			"source": "random",
			"scale": 24.0,
			"entries": [
				$spin
			]
		}
	}
}
"@
			} else {
				$rows = if ($gem -or -not $plain.ContainsKey($pieceId)) { Rows $art[$pieceId] } else { , [string[]]$plain[$pieceId] }
				Draw $rows $palette (Join-Path $tex "item\$id.png")
				Write-Json (Join-Path $assets "models\item\$id.json") "{ `"parent`": `"minecraft:item/generated`", `"textures`": { `"layer0`": `"bling:item/$id`" } }"
				Write-Json (Join-Path $assets "items\$id.json") "{ `"model`": { `"type`": `"minecraft:model`", `"model`": `"bling:item/$id`" } }"
			}
		}
	}
}
foreach ($piece in $pieces) { $lang["piece.bling.$($piece[0])"] = $piece[2] }

# ---- The 3D jewelry on the player ----
# Every box of a piece samples its whole texture, so these are just the material with a little sparkle.
function Material-Texture($dark, $mid, $light, $path, $seed) {
	$rand = New-Object System.Random $seed
	$bmp = New-Object System.Drawing.Bitmap 32, 32, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
	for ($y = 0; $y -lt 32; $y++) {
		for ($x = 0; $x -lt 32; $x++) {
			$roll = $rand.NextDouble()
			$hex = if ($roll -lt 0.15) { $light } elseif ($roll -lt 0.3) { $dark } else { $mid }
			$bmp.SetPixel($x, $y, (Color $hex))
		}
	}
	Save-Bitmap $bmp $path
}
$seed = 1
foreach ($m in $metals) { Material-Texture $m[2] $m[3] $m[4] (Join-Path $tex "entity\jewelry\$($m[0]).png") ($seed++) }
foreach ($g in $gems) { Material-Texture $g[2] $g[3] $g[4] (Join-Path $tex "entity\jewelry\$($g[0]).png") ($seed++) }
Material-Texture 'd8d4c6' 'f4f1e6' 'ffffff' (Join-Path $tex 'entity\jewelry\watch_face.png') ($seed++)

# ---- Empty jewelry slot icons (solid gray outlines, like vanilla's armor slot icons) ----
$gray = @{ 'l' = '555555'; 'm' = '555555'; 'd' = '555555'; 'x' = '555555'; 'o' = '555555'; 'H' = '555555' }
Draw (Rows $art['earrings']) $gray (Join-Path $tex 'gui\sprites\container\slot\ears.png')
Draw (Rows $art['chain']) $gray (Join-Path $tex 'gui\sprites\container\slot\neck.png')
Draw (Rows $art['bracelet']) $gray (Join-Path $tex 'gui\sprites\container\slot\wrist.png')

# ---- Jeweler's bench ----
# Felt is f/e and gold y/w because hashtable keys ignore case.
$wood = @{ 'k' = '3a2414'; 'a' = '5a3a22'; 'b' = '7a5232'; 'c' = '9a6a42'; 'f' = '2e6b4a'; 'e' = '3f8a60'; 'y' = 'f2c53d'; 'w' = 'fff2a6'; 's' = 'c9c9c9'; 'g' = '4ee6dc' }
$benchTop = @'
kkkkkkkkkkkkkkkk
kbbbbbbbbbbbbbbk
kbffffffffffffbk
kbfeeeeeeeeeefbk
kbfeeewyeeeeefbk
kbfeeyeeyeeeefbk
kbfeeyeeyeegefbk
kbfeeeyyeeeeefbk
kbfeeeeeeeeeefbk
kbfeeeeeeeesefbk
kbfeeeeeeeeesfbk
kbfeeeeeeeeeefbk
kbffffffffffffbk
kbbbbbbbbbbbbbbk
kbcbcbcbcbcbcbbk
kkkkkkkkkkkkkkkk
'@
$benchFront = @'
kkkkkkkkkkkkkkkk
kcccccccccccccck
kbbbbbbbbbbbbbbk
kbkkkkkkkkkkkkbk
kbkcccccccccckbk
kbkccccwycccckbk
kbkbbbbbbbbbbkbk
kbkkkkkkkkkkkkbk
kbbbbbbbbbbbbbbk
kbkkkkkkkkkkkkbk
kbkcccccccccckbk
kbkccccwycccckbk
kbkbbbbbbbbbbkbk
kbkkkkkkkkkkkkbk
kbbbbbbbbbbbbbbk
kkkkkkkkkkkkkkkk
'@
$benchSide = @'
kkkkkkkkkkkkkkkk
kcccccccccccccck
kbbbbbbbbbbbbbbk
kbccccccccccccbk
kbccccccccccccbk
kbaaaaaaaaaaaabk
kbccccccccccccbk
kbccccccccccccbk
kbaaaaaaaaaaaabk
kbccccccccccccbk
kbccccccccccccbk
kbaaaaaaaaaaaabk
kbccccccccccccbk
kbccccccccccccbk
kbbbbbbbbbbbbbbk
kkkkkkkkkkkkkkkk
'@
Draw (Rows $benchTop) $wood (Join-Path $tex 'block\jewelers_bench_top.png')
Draw (Rows $benchFront) $wood (Join-Path $tex 'block\jewelers_bench_front.png')
Draw (Rows $benchSide) $wood (Join-Path $tex 'block\jewelers_bench_side.png')

Write-Json (Join-Path $assets 'models\block\jewelers_bench.json') @'
{
	"parent": "minecraft:block/orientable_with_bottom",
	"textures": {
		"top": "bling:block/jewelers_bench_top",
		"front": "bling:block/jewelers_bench_front",
		"side": "bling:block/jewelers_bench_side",
		"bottom": "bling:block/jewelers_bench_side"
	}
}
'@
$facings = [ordered]@{ 'north' = 0; 'east' = 90; 'south' = 180; 'west' = 270 }
$variants = ($facings.Keys | ForEach-Object { "`t`t`"facing=$_`": { `"model`": `"bling:block/jewelers_bench`", `"y`": $($facings[$_]) }" }) -join ",`n"
Write-Json (Join-Path $assets 'blockstates\jewelers_bench.json') "{`n`t`"variants`": {`n$variants`n`t}`n}"
Write-Json (Join-Path $assets 'items\jewelers_bench.json') '{ "model": { "type": "minecraft:model", "model": "bling:block/jewelers_bench" } }'
Write-Json (Join-Path $data 'loot_table\blocks\jewelers_bench.json') @'
{
	"type": "minecraft:block",
	"pools": [
		{
			"rolls": 1,
			"condition": { "type": "minecraft:survives_explosion" },
			"entries": [ { "type": "minecraft:item", "name": "bling:jewelers_bench" } ]
		}
	]
}
'@
Write-Json (Join-Path $data 'recipe\jewelers_bench.json') @'
{
	"type": "minecraft:crafting_shaped",
	"category": "misc",
	"pattern": [ "IGI", "PPP", "P P" ],
	"key": { "I": "minecraft:iron_ingot", "G": "minecraft:gold_ingot", "P": "#minecraft:planks" },
	"result": { "id": "bling:jewelers_bench", "count": 1 }
}
'@
Write-Json (Join-Path $data 'advancement\recipes\misc\jewelers_bench.json') @'
{
	"parent": "minecraft:recipes/root",
	"criteria": {
		"has_gold": { "conditions": { "items": [ { "items": "minecraft:gold_ingot" } ] }, "trigger": "minecraft:inventory_changed" },
		"has_the_recipe": { "conditions": { "recipes": "bling:jewelers_bench" }, "trigger": "minecraft:recipe_unlocked" }
	},
	"requirements": [ [ "has_the_recipe", "has_gold" ] ],
	"rewards": { "recipes": [ "bling:jewelers_bench" ] }
}
'@
# ---- The bench screen: a standard container panel with metal and gem slots, piece buttons, and the result ----
$panel = New-Object System.Drawing.Bitmap 256, 256, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($panel)
function Fill($x, $y, $w, $h, $hex) { $g.FillRectangle((New-Object System.Drawing.SolidBrush (Color $hex)), $x, $y, $w, $h) }
function Bevel($x, $y, $w, $h, $topLeft, $bottomRight, $fill) {
	Fill $x $y $w $h $fill
	Fill $x $y ($w - 1) 1 $topLeft; Fill $x $y 1 ($h - 1) $topLeft
	Fill ($x + 1) ($y + $h - 1) ($w - 1) 1 $bottomRight; Fill ($x + $w - 1) ($y + 1) 1 ($h - 1) $bottomRight
}
function Slot($x, $y) { Bevel ($x - 1) ($y - 1) 18 18 '373737' 'ffffff' '8b8b8b' }

Fill 0 0 176 166 '000000'
Fill 1 1 174 164 'c6c6c6'
Fill 1 1 173 2 'ffffff'; Fill 1 1 2 163 'ffffff'
Fill 3 163 172 2 '555555'; Fill 173 3 2 162 '555555'

Slot 20 24; Slot 20 46; Slot 143 35
for ($row = 0; $row -lt 3; $row++) { for ($col = 0; $col -lt 9; $col++) { Slot (8 + $col * 18) (84 + $row * 18) } }
for ($col = 0; $col -lt 9; $col++) { Slot (8 + $col * 18) 142 }
# Arrow from the buttons to the result.
Fill 128 41 8 3 '8b8b8b'
for ($i = 0; $i -lt 5; $i++) { Fill (136 + $i) (38 + $i) 1 (9 - 2 * $i) '8b8b8b' }
$g.Dispose()
# The corners of a vanilla panel are cut off diagonally.
foreach ($p in @(@(0, 0), @(1, 0), @(0, 1), @(175, 0), @(174, 0), @(175, 1), @(0, 165), @(1, 165), @(0, 164), @(175, 165), @(174, 165), @(175, 164))) {
	$panel.SetPixel($p[0], $p[1], [System.Drawing.Color]::Transparent)
}
Save-Bitmap $panel (Join-Path $tex 'gui\container\jewelers_bench.png')

function Button($name, $topLeft, $bottomRight, $fill) {
	$bmp = New-Object System.Drawing.Bitmap 20, 20, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
	$script:g = [System.Drawing.Graphics]::FromImage($bmp)
	Fill 0 0 20 20 '000000'
	Bevel 1 1 18 18 $topLeft $bottomRight $fill
	$script:g.Dispose()
	Save-Bitmap $bmp (Join-Path $tex "gui\sprites\container\jewelers_bench\$name.png")
}
Button 'button' 'ffffff' '555555' 'a8a8a8'
Button 'button_highlighted' 'ffffff' '555555' 'c8d4ff'
Button 'button_selected' '373737' 'ffffff' '8fa0e0'

# ---- Jewelry display case: a wooden counter with red velvet under a glass top ----
# r/q/v are velvet, n is see-through glass and j a glint on it (both partly transparent, so the game draws them as glass).
$case = @{ 'k' = '3a2414'; 'a' = '5a3a22'; 'b' = '7a5232'; 'c' = '9a6a42'; 'y' = 'f2c53d'; 'w' = 'fff2a6'
	'r' = '6e0f1d'; 'q' = '8f1627'; 'v' = 'ad2238'; 'n' = '38c8e8f0'; 'j' = '99ffffff' }
# Only the bottom 11 rows of the sides show; the glass covers the rest.
$caseSide = @'
kkkkkkkkkkkkkkkk
kkkkkkkkkkkkkkkk
kkkkkkkkkkkkkkkk
kkkkkkkkkkkkkkkk
kkkkkkkkkkkkkkkk
kcccccccccccccck
kbbbbbbbbbbbbbbk
kbccccccccccccbk
kbcbbbbbbbbbbcbk
kbcbccccccccbcbk
kbcbccccccccbcbk
kbcbccccccccbcbk
kbcbbbbbbbbbbcbk
kbccccccccccccbk
kbbbbbbbbbbbbbbk
kkkkkkkkkkkkkkkk
'@
$caseFront = Rows $caseSide
$caseFront[10] = 'kbcbcccywcccbcbk'
$caseFront[11] = 'kbcbcccyycccbcbk'
$caseVelvet = @'
bbbbbbbbbbbbbbbb
bqqqqqqqqqqqqqqb
bqvqqqqrqqqqqvqb
bqqqqqqqqqqqqqqb
bqqqrqqqqqqqqqqb
bqqqqqqqqvqqrqqb
bqqqqqqqqqqqqqqb
bqqvqqqqqqqqqqqb
bqqqqqqrqqqqqqqb
bqqqqqqqqqqqvqqb
bqrqqqqqqqqqqqqb
bqqqqqvqqqqqqqqb
bqqqqqqqqqqrqqqb
bqvqqqqqqqqqqqqb
bqqqqqqqqqqqqvqb
bbbbbbbbbbbbbbbb
'@
$caseGlassTop = @'
aaaaaaaaaaaaaaaa
annnnnnnnnnnnnna
annjnnnnnnnnnnna
anjnnnnnnnnnnnna
annnnnnnnnnnnnna
annnnnnnnnnnnnna
annnnnnnnnnnnnna
annnnnnnnnnnnnna
annnnnnnnnnnnnna
annnnnnnnnnnnnna
annnnnnnnnnnnnna
annnnnnnnnnnnnna
annnnnnnnnnnjnna
annnnnnnnnnnnjna
annnnnnnnnnnnnna
aaaaaaaaaaaaaaaa
'@
# Only the top 5 rows are used: the glass walls are 5 pixels tall.
$caseGlassSide = @'
aaaaaaaaaaaaaaaa
annjnnnnnnnnnnna
anjnnnnnnnnnnnna
annnnnnnnnnnnnna
aaaaaaaaaaaaaaaa
................
................
................
................
................
................
................
................
................
................
................
'@
Draw (Rows $caseSide) $case (Join-Path $tex 'block\jewelry_display_case_side.png')
Draw $caseFront $case (Join-Path $tex 'block\jewelry_display_case_front.png')
Draw (Rows $caseVelvet) $case (Join-Path $tex 'block\jewelry_display_case_velvet.png')
Draw (Rows $caseGlassTop) $case (Join-Path $tex 'block\jewelry_display_case_glass_top.png')
Draw (Rows $caseGlassSide) $case (Join-Path $tex 'block\jewelry_display_case_glass_side.png')

Write-Json (Join-Path $assets 'models\block\jewelry_display_case.json') @'
{
	"parent": "minecraft:block/block",
	"textures": {
		"particle": "bling:block/jewelry_display_case_side",
		"side": "bling:block/jewelry_display_case_side",
		"front": "bling:block/jewelry_display_case_front",
		"velvet": "bling:block/jewelry_display_case_velvet",
		"glass_top": "bling:block/jewelry_display_case_glass_top",
		"glass_side": "bling:block/jewelry_display_case_glass_side"
	},
	"elements": [
		{
			"from": [ 0, 0, 0 ],
			"to": [ 16, 11, 16 ],
			"faces": {
				"down": { "uv": [ 0, 0, 16, 16 ], "texture": "#side", "cullface": "down" },
				"up": { "uv": [ 0, 0, 16, 16 ], "texture": "#velvet" },
				"north": { "uv": [ 0, 5, 16, 16 ], "texture": "#front", "cullface": "north" },
				"south": { "uv": [ 0, 5, 16, 16 ], "texture": "#side", "cullface": "south" },
				"west": { "uv": [ 0, 5, 16, 16 ], "texture": "#side", "cullface": "west" },
				"east": { "uv": [ 0, 5, 16, 16 ], "texture": "#side", "cullface": "east" }
			}
		},
		{
			"from": [ 0, 11, 0 ],
			"to": [ 16, 16, 16 ],
			"faces": {
				"up": { "uv": [ 0, 0, 16, 16 ], "texture": "#glass_top", "cullface": "up" },
				"north": { "uv": [ 0, 0, 16, 5 ], "texture": "#glass_side", "cullface": "north" },
				"south": { "uv": [ 0, 0, 16, 5 ], "texture": "#glass_side", "cullface": "south" },
				"west": { "uv": [ 0, 0, 16, 5 ], "texture": "#glass_side", "cullface": "west" },
				"east": { "uv": [ 0, 0, 16, 5 ], "texture": "#glass_side", "cullface": "east" }
			}
		}
	]
}
'@
$variants = ($facings.Keys | ForEach-Object { "`t`t`"facing=$_`": { `"model`": `"bling:block/jewelry_display_case`", `"y`": $($facings[$_]) }" }) -join ",`n"
Write-Json (Join-Path $assets 'blockstates\jewelry_display_case.json') "{`n`t`"variants`": {`n$variants`n`t}`n}"
Write-Json (Join-Path $assets 'items\jewelry_display_case.json') '{ "model": { "type": "minecraft:model", "model": "bling:block/jewelry_display_case" } }'
Write-Json (Join-Path $data 'loot_table\blocks\jewelry_display_case.json') @'
{
	"type": "minecraft:block",
	"pools": [
		{
			"rolls": 1,
			"condition": { "type": "minecraft:survives_explosion" },
			"entries": [ { "type": "minecraft:item", "name": "bling:jewelry_display_case" } ]
		}
	]
}
'@
Write-Json (Join-Path $data 'recipe\jewelry_display_case.json') @'
{
	"type": "minecraft:crafting_shaped",
	"category": "misc",
	"pattern": [ "GGG", "PRP", "PPP" ],
	"key": { "G": "minecraft:glass", "R": "minecraft:red_wool", "P": "#minecraft:planks" },
	"result": { "id": "bling:jewelry_display_case", "count": 1 }
}
'@
Write-Json (Join-Path $data 'advancement\recipes\misc\jewelry_display_case.json') @'
{
	"parent": "minecraft:recipes/root",
	"criteria": {
		"has_bench": { "conditions": { "items": [ { "items": "bling:jewelers_bench" } ] }, "trigger": "minecraft:inventory_changed" },
		"has_the_recipe": { "conditions": { "recipes": "bling:jewelry_display_case" }, "trigger": "minecraft:recipe_unlocked" }
	},
	"requirements": [ [ "has_the_recipe", "has_bench" ] ],
	"rewards": { "recipes": [ "bling:jewelry_display_case" ] }
}
'@
Write-Json (Join-Path $root 'data\minecraft\tags\block\mineable\axe.json') '{ "values": [ "bling:jewelers_bench", "bling:jewelry_display_case" ] }'

# The case's screen: like a hopper's, with 4 slots on a strip of velvet.
$casePanel = New-Object System.Drawing.Bitmap 256, 256, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$script:g = [System.Drawing.Graphics]::FromImage($casePanel)
Fill 0 0 176 133 '000000'
Fill 1 1 174 131 'c6c6c6'
Fill 1 1 173 2 'ffffff'; Fill 1 1 2 130 'ffffff'
Fill 3 130 172 2 '555555'; Fill 173 3 2 129 '555555'
Bevel 48 15 80 26 '4a0a14' 'c43a50' '8f1627'
for ($i = 0; $i -lt 4; $i++) { Slot (53 + $i * 18) 20 }
for ($row = 0; $row -lt 3; $row++) { for ($col = 0; $col -lt 9; $col++) { Slot (8 + $col * 18) (51 + $row * 18) } }
for ($col = 0; $col -lt 9; $col++) { Slot (8 + $col * 18) 109 }
$script:g.Dispose()
foreach ($p in @(@(0, 0), @(1, 0), @(0, 1), @(175, 0), @(174, 0), @(175, 1), @(0, 132), @(1, 132), @(0, 131), @(175, 132), @(174, 132), @(175, 131))) {
	$casePanel.SetPixel($p[0], $p[1], [System.Drawing.Color]::Transparent)
}
Save-Bitmap $casePanel (Join-Path $tex 'gui\container\jewelry_display_case.png')

# ---- Mod icon: the gold diamond chain, 8 times the size ----
Draw (Rows $art['chain']) @{ 'd' = 'b0781c'; 'm' = 'f2c53d'; 'l' = 'fff2a6'; 'x' = '1d9e97'; 'o' = '4ee6dc'; 'H' = 'd7fff9' } (Join-Path $assets 'icon.png') 8

# ---- Names ----
$lang['block.bling.jewelers_bench'] = "Jeweler's Bench"
$lang['container.bling.jewelers_bench'] = "Jeweler's Bench"
$lang['block.bling.jewelry_display_case'] = 'Jewelry Display Case'
$lang['container.bling.jewelry_display_case'] = 'Jewelry Display Case'
$lang['gui.bling.ingots'] = '%s x %s'
$lang['gui.bling.any_ingots'] = '%s ingots of any metal'
$lang['gui.bling.gem_optional'] = 'Add a gem if you like'
$lang['gui.bling.no_gem'] = 'No gem'
$lang['gui.bling.gem_required'] = 'Needs a gem'
$lang['gui.bling.diamonds'] = 'Needs %s diamonds'
$langLines = ($lang.Keys | ForEach-Object { "`t`"$_`": `"$($lang[$_])`"" }) -join ",`n"
Write-Json (Join-Path $assets 'lang\en_us.json') "{`n$langLines`n}"

Write-Host "Generated $($itemIds.Count) jewelry items plus the jeweler's bench."