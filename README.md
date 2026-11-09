# Assignment 4. Patient Zero

- Student ID: **250304**
- Name: **Dariya Zhagufarova**
- Group: **SE-2532**
- Language: Java 17 or newer

## Run

Run these commands from the repository folder:

```sh
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

The personal input files are included. Regenerate them with:

```sh
java tools/MakeVariant.java 250304 data
```

Open `output/outbreak.html` in a browser to see the interactive lab.

## Program output

```text
Mutations: 67
Shared core: 921
Marker hits: 5
First hit: 6763
Border length: 4
Shared words: 426
Tests needed: 31
Cheapest testing: 129
Longest chain: 22
```

![Personal outbreak report](output/outbreak.png)

## Questions

### Mission 1: edit distance

`d[i][j]` is the smallest number of edits that change the first `i` letters of A into the first `j` letters of B. The first row and column count insertions or deletions from an empty string; each other cell takes the minimum of deleting, inserting, or replacing the last letter, with zero replacement cost when the letters already match.

### Mission 2: shared core

A subsequence keeps the order of letters but allows gaps; a substring must be consecutive. Insertions and deletions shift positions, so letters can belong to the common subsequence even when they are not at the same index in the two strains.

### Mission 3: prefix function and KMP

`pi[i]` is the length of the longest proper prefix of the marker ending at `i` that is also its suffix. On a mismatch, KMP uses these borders to shorten the matched pattern prefix without moving the text index backwards; after a complete match it also keeps the border so overlaps count. The personal marker is `CCTATGCCTA`, whose border length is **4** because `CCTA` is both its prefix and suffix.

### Mission 4: rolling hash

A hash is only a number, so different words may have the same hash; the code checks letters before counting a match. Windows in B are grouped by hash, and each window in A counts at most once, even if it appears several times in B. Many collisions require many candidate comparisons and can make the worst-case time quadratic in the number of windows, multiplied by the window length.

### Mission 5: testing people

`dp[v][0]` is the cheapest cover of the subtree when person v is not tested, and `dp[v][1]` is the cheapest cover when v is tested. An untested parent forces every child to be tested; a tested parent allows the cheaper state of each child, and a second traversal reconstructs the chosen people. Choosing the person with the most links can fail: for a three-leaf star with centre cost 10 and each leaf cost 1, testing the centre costs 10 but testing all leaves costs 3.

The longest chain is found by keeping the two greatest downward heights at each person and considering their sum. It is measured in links, so a single person has chain length zero.

## Timing (Mission 7)

Measured on 9 November 2026 with the unchanged `Bench`, Java 17 on the local Mac. Values are milliseconds, averaged over three runs.

| Test | Size | Time (ms) |
|---|---|---|
| Naive search | 20,000 / 40,000 / 80,000 | 7.4 / 12.0 / 24.6 |
| KMP | 20,000 / 40,000 / 80,000 | 1.3 / 0.8 / 0.4 |
| Edit distance | 500 / 1,000 / 2,000 / 4,000 | 3.6 / 3.0 / 10.0 / 35.6 |

The naive search approximately doubles between the two larger text sizes, with the pattern length fixed at 2,000. Edit distance takes roughly four times as much work when both strain lengths double, consistent with filling a two-dimensional table. KMP's tiny measured times decrease here because JVM warm-up and measurement noise dominate; its theoretical time remains linear, so these measurements do not imply that larger inputs are intrinsically faster.

## Complexity

Let a and b be the strain lengths, n the genome length, m the marker length, k the window length, h the number of marker hits, and v the number of people.

| Algorithm | Time | Extra space | Assumptions |
|---|---|---|---|
| Edit distance | O((a+1)(b+1)) | O((a+1)(b+1)) | Full DP table; O(n²) for two length-n strains |
| LCS | O((a+1)(b+1)) | O((a+1)(b+1)) | Full DP table |
| Prefix function | O(m) | O(m) | Border fallback is amortized linear |
| KMP including prefix function | O(n+m) | O(m+h) | Includes returned hit positions |
| Rabin-Karp shared words | Expected O(a+b+k+c·k); worst O(a+b+k+a·b·k) | O(b) | c candidate comparisons; expected constant-time hash-map access; every candidate is verified |
| Tree minimum cover and reconstruction | O(v) | O(v) | Tree has v−1 edges; recursive depth at most v |
| Tree longest chain | O(v) | O(v) | Two greatest child heights per vertex |

For the assignment k is fixed at 12. With few collisions, Rabin-Karp is expected linear in the strain lengths; repeated or colliding candidates can increase verification work. Recursive tree traversal is appropriate for the supplied trees (about 100 people); extremely deep arbitrary trees would need an explicit stack.

## Verification

```sh
java tools/Check.java
javac -encoding UTF-8 -cp out -d out tests/AlgorithmTest.java
java -cp out AlgorithmTest
```

- All nine output lines pass on `tiny`, `small`, and `dense`.
- All nine personal results agree with the independent browser lab checks.
- 150 deterministic randomized trials compare against exhaustive edit/LCS/tree-cover calculations, direct string comparisons, and all-pairs tree distances.
- Explicit checks cover overlapping matches and repeated copied words.

`Main`, `Input`, `Render`, `Generator`, `Bench`, the lab page, tools and examples come from the provided starter and are unchanged. The implementation is in `Strains.java`, `Marker.java`, and `Outbreak.java`.

## Submission

Public repository: https://github.com/dardau/patient-zero

Submit this link in Moodle. Uploading the link in Moodle is a separate manual step.
