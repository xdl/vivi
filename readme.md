# vivi

Command line based chord progression looping tool for harmonisation, improvisation or ear training (originally intended for lead guitar practice).

Demo [here](https://imgur.com/a/YqM1n54).

## Format

Chords are specified according to [roman numerals](https://en.wikipedia.org/wiki/Roman_numeral_analysis).

Only the non-diminished chords of major and minor scales are currently supported:

* Major: `I`, `ii`, `iii`, `IV`, `V`, `vi` (and `bVII`)
* Minor: `i`, `III`, `iv`, `v`, `VI`, `VII`

i.e. no coloured (e.g. `Vadd2`), other non-diatonic or seven (e.g. `V7`) chords.

The chord progressions to play are specified in an [org file](https://en.wikipedia.org/wiki/Org-mode).

Chord progressions are denoted with the `***` sub-heading (only works with 4/4 time signature at the moment, so make sure to specify 4 chords), and any additional info (e.g. associated songs) to be displayed currently needs to be in bullet points (`-`):

```
*** IV I V V

- The Verve - Lucky Man (verse)
- Future Islands - A Song For Our Grandfathers
```

See `/data/pop.org` or `/data/sample.org` for concrete example files.

## Installation

Uses [leiningen](https://leiningen.org/) as the package manager (requires a Java JDk (e.g. >= OpenJDK 8) to be available).

Install dependencies with:

    lein deps
    
Then run with:

    lein run <path/to/progressions>
    
e.g.

    lein run data/pop.org
    
## Usage

Command available in `vivi` are:

```
h: Help
r: Reveal chord progression and key

c: Shuffle chord progression
cs: Select chord progression

k: Shuffle key
ks: Select key

p: Play/Pause
pd: Toggle drums

f: Faster
s: Slower

q: Quit
```

What to do next is up to you, but personally I:

* Practice identifying the chord progression and possible root
* Solo improvisation (could be useful to have the [major](https://www.guitarlessons.com/guitar-lessons/exercises-and-fretboard-navigation/caged-major-scale-sequence) and [minor](https://www.guitarlessons.com/guitar-lessons/exercises-and-fretboard-navigation/caged-minor-guitar-chords) CAGED diagrams open for reference)
    
## Tests

Run with `lein test`

## Extending

Potential areas of exploration and suggested ways to implement:

* Coloured/jazz-y chords: [Documentation](https://github.com/overtone/overtone/blob/master/src/overtone/synth/stringed.clj#L194) for extending the chord frets, and then the `/utils.clj/get-chord` function to connect to it
* Different time signatures: See how the drum timings and measures are hardcoded in the `prog_player.clj/play-loop` function and parameterise them (e.g. according to how many chords are in the progression)
* Different parsing logic of org file: See `chord_parse.clj` and the example in the tests

## Troubleshooting

On Linux (the only OS I've tested on) - make sure there's a JACK server running:

    sudo apt install qjackctl
    
`qjackctl` opens the GUI for JACK - make sure that the server is started.

### Shutting Down

* Sometimes an errant Java process will be left running after closing; check with `ps -Cf java` and kill accordingly with `kill -9 <PID>`
