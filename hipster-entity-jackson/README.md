# hipster-entity-jackson

Jackson integration for `hipster-entity` array-backed views.

## JMH benchmark results (2026-04-03)

Benchmark was run in `hipster-entity-jackson` with:
- 3 forks
- 6 warmup iterations (2s each)
- 8 measurement iterations (2s each)
- `Mode.Throughput` (ops/ms)

### Deserialization

| Path                               | Throughput (ops/ms) | 99.9% CI           |
| ---------------------------------- | ------------------- | ------------------ |
| default Jackson POJO (`PersonDto`) | 2350.12             | [≈0, 6287.29]      |
| `EntityJacksonMapper` + `ViewMeta` | 938.56              | [≈0, 2237.70]      |

### Serialization

| Path                                        | Throughput (ops/ms) | 99.9% CI         |
| ------------------------------------------- | ------------------- | ---------------- |
| default Jackson POJO (`PersonDto`)          | 3987.26             | [≈0, 15385.57]    |
| `EntityJacksonMapper` + `ViewMeta`          | 3593.36             | [3148.82, 4037.89]|
| `EntityJacksonViewSerializer` (optimized)   | 3808.14             | [≈0, 8761.76]     |
| `PersonSummaryGeneratedSerializer` (boiler) | 3853.31             | [3082.39, 4624.23]|

### Observations

- `EntityJacksonMapper` path is optimized for interface-based view proxy data and avoids full POJO reflection.
- For deserialization, the `EntityJacksonMapper` path is still slower than direct Jackson POJO due extra `ViewMeta` construct and array dispatch, but semantic advantages (no reflection, generated schemas) can make this a strong tradeoff.
- Serialization-by-view path performs competitively with reflection POJO, with room for further micro-optimizations.

### Conclusions

- `EntityJacksonViewSerializer` now achieves throughput on par with generated concrete serializer code and approaches POJO performance in serialization.
- A dedicated record type is not required to reach this performance; interface-based views with the proxy + `ViewMeta` pipeline are sufficient.
- This validates the design goal that an interface-first, array-backed model can be both flexible and high-performance, without forcing records into user APIs.
- This helps reduce number of classes, and improve build time. Generated boilerplate can still be good choice for hot paths

## Running the benchmark

```bash
bun run scripts/run-jmh.js --include ".*EntityJacksonJmhBenchmark.*" --forks 3 \
  --warmup-iterations 6 --measurement-iterations 8 --warmup-time 2s --measurement-time 2s
```
