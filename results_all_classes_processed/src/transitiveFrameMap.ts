export const transitiveFrameMap: Record<string, [string, string]> = {
  'http://dbpedia.org/ontology/presenter	': [
    'http://dbpedia.org/ontology/TelevisionShow	',
    'http://dbpedia.org/ontology/Person',
  ],
  'http://dbpedia.org/ontology/creator	': [
    'http://dbpedia.org/ontology/TelevisionShow	',
    'http://dbpedia.org/ontology/Person  ',
  ],
  'http://dbpedia.org/ontology/product	': [
    'http://dbpedia.org/ontology/Company	',
    'http://dbpedia.org/ontology/Software      ',
  ],
  'http://dbpedia.org/ontology/musicComposer	': [
    'http://dbpedia.org/ontology/Film	',
    'http://dbpedia.org/ontology/Person      ',
  ],
  'http://dbpedia.org/property/artist	': [
    'http://dbpedia.org/ontology/Work	',
    'http://dbpedia.org/ontology/Person              ',
  ],
  'http://dbpedia.org/ontology/publisher	': [
    'http://dbpedia.org/ontology/Work	',
    'http://dbpedia.org/ontology/Person          ',
  ],
  'http://dbpedia.org/ontology/crosses	': [
    'http://dbpedia.org/ontology/Bridge	',
    'http://dbpedia.org/ontology/River           ',
  ],
  'http://dbpedia.org/ontology/award	': [
    'http://dbpedia.org/ontology/Scientist	',
    'http://dbpedia.org/ontology/Award         ',
  ],
  'http://dbpedia.org/ontology/budget	': [
    'http://dbpedia.org/ontology/Film',
    'xsd:double                                       ',
  ],
  'http://dbpedia.org/ontology/writer	': [
    'http://dbpedia.org/ontology/Song	',
    'http://dbpedia.org/ontology/Person              ',
  ],
  'http://dbpedia.org/ontology/composer	': [
    'http://dbpedia.org/ontology/TelevisionShow	',
    'http://dbpedia.org/ontology/Person  ',
  ],
  'http://dbpedia.org/ontology/author	': [
    'http://dbpedia.org/ontology/Book	',
    'http://dbpedia.org/ontology/Person              ',
  ],
  'http://dbpedia.org/ontology/almaMater	': [
    'http://dbpedia.org/ontology/Person	',
    'http://dbpedia.org/ontology/University    ',
  ],
  'http://dbpedia.org/ontology/operator	': [
    'http://dbpedia.org/ontology/Company	',
    'http://dbpedia.org/ontology/LaunchPad     ',
  ],
  'http://dbpedia.org/property/nickname	': [
    'http://dbpedia.org/ontology/Person',
    'xsd:String                                   ',
  ],
  'http://dbpedia.org/ontology/foundedBy	': [
    'http://dbpedia.org/ontology/Company	',
    'http://dbpedia.org/ontology/Pope        ',
  ],
  'http://dbpedia.org/ontology/founder	': [
    'http://dbpedia.org/ontology/Publisher	',
    'http://dbpedia.org/ontology/Person      ',
  ],
  'http://dbpedia.org/ontology/firstAscentPerson	': [
    'http://dbpedia.org/ontology/Mountain	',
    'http://dbpedia.org/ontology/Person',
  ],
  'http://dbpedia.org/ontology/discoverer	': [
    'http://dbpedia.org/ontology/CelestialBody	',
    'http://dbpedia.org/ontology/Person',
  ],
  'http://dbpedia.org/property/portrayer	': [
    'http://dbpedia.org/ontology/FictionalCharacter	',
    'http://dbpedia.org/ontology/Person',
  ],
  'http://dbpedia.org/ontology/director	': [
    'http://dbpedia.org/ontology/Film	',
    'http://dbpedia.org/ontology/Person                ',
  ],
  'http://dbpedia.org/ontology/influencedBy	': [
    'http://dbpedia.org/ontology/Person	',
    'http://dbpedia.org/ontology/Person          ',
  ],
  'http://dbpedia.org/ontology/influenced	': [
    'http://dbpedia.org/ontology/Artist	',
    'http://dbpedia.org/ontology/Artist            ',
  ],
  'http://dbpedia.org/ontology/architect	': [
    'http://dbpedia.org/ontology/Bridge	',
    'http://dbpedia.org/ontology/Person            ',
  ],
  'http://dbpedia.org/ontology/spouse	': [
    'http://dbpedia.org/ontology/Person	',
    'http://dbpedia.org/ontology/Person                ',
  ],
};
