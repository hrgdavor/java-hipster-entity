# java-hipster-entity

`hipster-entity` is an interface-first Java entity model focused on extracting strong structural metadata that can be reused across runtime helpers, code generation, and higher-level tooling.

The project favors explicit metadata and predictable contracts over reflection-heavy runtime discovery. Runtime helpers such as proxies, builders, and factories are layered components that can be adopted incrementally.

## Implementation direction

- Proxy-backed builders and views are the baseline implementation path.
- Change tracking is ordinal/bitmask based for compact and predictable behavior.
- Generated/materialized implementations are targeted optimizations for measured hotspots, not the default.

## Documentation

- [Project docs index](doc/README.md)
- [Architecture decisions](doc/architecture/DECISIONS.md)
- [Roadmap](doc/roadmap/README.md)
- [User documentation](doc/user/README.md)

