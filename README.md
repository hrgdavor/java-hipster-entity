TODO 
- proxy for entity view that reads from Entity
- automatic creation of builder interface
- proxy for builder that uses EntityUpdateArray internally
- proxy for builder with change tracking EntityUpdateTrackingArray internally
- Use maybe factory for those classes, so proxy variants can be replaced with generated implementations if some need performance that way.


# Performance 

Many use cases performance of the interface implementation is not relevant that much,
so initially it is a good idea to implement the bridge with proxies.

Records can be used to internal caching and immutability requirements.

For specific cases concrete implementations can be generated into concrete Java classes to 
allow compiler to optimize the code further. Also specific use cases can be coded manually
as the interface only definex how your code sees, data, actual access is up to you, and
what you want to do with metadata.

