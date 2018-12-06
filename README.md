# Traffic Engineering Impact Simulator

Simulator written in Java to test the impact of traffic engineering on the internet. For more information please explore the attached thesis.

Traffic Engineering (TE) can have a significant impact on the overall scalability of the Internet depending on how these techniques are used.To better understand these effects, we built an impact simulator application to test TE configurations and immediately see the effects on the global routing system.
As input, the application uses the Internet’s inferred topology, from there it is possible to navigate to a specific AS and test some TE configurations. Namely it is possible to simulate the changes in route type from advertising only to a subset of the providers and it is also possible to prepend to a specific amount some of the advertised routes routes and extract the number of ASes that will elect a route that has an associated higher number of hops. All of this using a simple but effective interface.

![Alt text](https://raw.githubusercontent.com/manuelspinto/TEImpactSimulator/master/simulator_sreenshot.PNG)

# Example
Let us consider a practical example to help us understand how this works. AS 38993 is registered on the RIPE database as RENOVA-PT-AS, belonging to the Portuguese company that produces paper consumption goods. An input in the Find field for this AS shows that this company gets its Internet access from two providers, VODAFONE-PT (AS12353) and NOS-COMUNICACOES (AS2860). Imagine that
RENOVA wants to use TE to balance inbound traffic on both links.
One way to do it is by using PD with selective advertisements therefore, the route for the deaggregated prefix is only advertised to one of the providers. To simulate this on the application, we click on one provider and press the ADD button. If we then press the Scoped button, the elected route type is computed for all nodes taking only into account the use of this link and is then compared to the no-TE situation displaying the changes on the screen for this particular configuration.

![Alt text](https://raw.githubusercontent.com/manuelspinto/TEImpactSimulator/master/te_example.PNG)
