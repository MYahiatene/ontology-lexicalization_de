import {
  nounHeader,
  transitiveVerbHeader,
  intransitiveVerbHeader,
  attributeAdjHeader,
  gradableAdjHeader,
} from './headers';
import * as fs from 'fs';
import {lemonObject} from './index';
import {result} from 'lodash';

const {PassThrough} = require('stream');
const {createReadStream, createWriteStream} = require('fs');
const {convertArrayToCSV} = require('convert-array-to-csv');
import {nounMap} from './nounMap';
import {transitiveFrameMap} from './transitiveFrameMap';
import {intransitiveFrameMap} from './intransitiveFrameMap';
import {ReadStream} from 'fs';
import {emit, on} from 'cluster';
import util from 'util';

export function createNewCsvFilesNounPPFrame(
  csvFile: string,
  obj: lemonObject
) {
  const label = obj.label.charAt(0).toUpperCase() + obj.label.slice(1);
  let wiktionaryObj: any = readAndParse('noun', label);
  /*  try {
      wiktionaryObj = fs.readFileSync(
        '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
      );
    } catch (e) {
      console.log(e);
    }*/
  const csvRow = convertArrayToCSV([
    [
      obj.sense,
      'noun',
      wiktionaryObj?.['head_templates']?.at(0)?.args?.g ?? '', //'genus'
      wiktionaryObj?.forms
        ?.filter((form: any) =>
          ['nominative', 'singular'].every((type: any) =>
            form['tags'].includes(type)
          )
        )
        ?.at(0)?.form ?? '-', //'Nominativ Singular'
      wiktionaryObj?.forms
        ?.filter((form: any) =>
          ['nominative', 'plural'].every((type: any) =>
            form['tags'].includes(type)
          )
        )
        ?.at(0)?.form ?? '-', //'Nominativ Plural'
      wiktionaryObj?.forms
        ?.filter((form: any) =>
          ['accusative', 'singular'].every((type: any) =>
            form['tags'].includes(type)
          )
        )
        ?.at(0)?.form ?? '-', //'Akkusativ Singular'
      wiktionaryObj?.forms
        ?.filter((form: any) =>
          ['dative', 'singular'].every((type: any) =>
            form['tags'].includes(type)
          )
        )
        ?.at(0)?.form ?? '-', //'Dativ Singular'
      wiktionaryObj?.forms
        ?.filter((form: any) =>
          ['genetive', 'singular'].every((type: any) =>
            form['tags'].includes(type)
          )
        )
        ?.at(0)?.form ?? '-', //'Genetiv Singular'
      'von',
      'NounPPFrame',
      'range',
      'domain',
      '1',
      Object.values(obj.references[0])[0],
      nounMap[Object.values(obj.references[0])[0]]?.[0] ?? '-', //'domain', //todo:
      nounMap[Object.values(obj.references[0])[0]]?.[1] ?? '-', //todo:
      /*        'domain_article', //todo:
                'domain_written_singular', //todo:
                'domain_written_plural', //todo:
                'rangeArticle', //todo:
                'range_written_singular', //todo:
                'range_written_plural', //todo:*/
    ],
  ]);

  fs.appendFileSync(csvFile, csvRow, 'utf-8');
}

export async function createNewCsvFilesTransitiveFrame(
  csvFile: string,
  obj: lemonObject
) {
  let wiktionaryObj: any = undefined;
  await readAndParse('noun', obj.label);
  try {
    wiktionaryObj = fs.readFileSync(
      '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
    );
  } catch (e) {
    console.log(e);
  }

  const csvRow = convertArrayToCSV([
    [
      obj.sense, //'LemonEntry',
      'verb', //partOfSpeech',
      '-', //'writtenFormInfinitive', //'writtenForm3rdPresent', //'writtenFormPast', //'writtenFormPerfect',
      '-',
      '-',
      '-',
      'TransitiveFrame', //'SyntacticFrame',
      'range', //'subject',
      'domain', //'directObject',
      '1', //'sense',
      Object.values(obj.references[0])[0], //'reference',
      transitiveFrameMap[Object.values(obj.references[0])[0]]?.[0] ?? '-', //'domain',
      transitiveFrameMap[Object.values(obj.references[0])[0]]?.[1] ?? '-', //'range',
      'von',
    ],
  ]);
  fs.appendFileSync(csvFile, csvRow, 'utf-8');
}

export async function createNewCsvFilesIntransitiveFrame(
  csvFile: string,
  obj: lemonObject
) {
  let wiktionaryObj: any = undefined;
  await readAndParse('verb', obj.label);
  try {
    wiktionaryObj = fs.readFileSync(
      '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
    );
  } catch (e) {
    console.log(e);
  }
  const csvRow = convertArrayToCSV([
    [
      obj.sense,
      'verb', //'writtenFormInfinitive', //todo: //'writtenFormThridPerson', //todo: //'writtenFormPast', //todo: //'writtenFormPast', //todo:
      '-',
      '-',
      '-',
      '-',
      'in', //'preposition', //todo:
      'IntransitivePPFrame', //'SyntacticFrame', //todo:
      'domain', //'subject',
      'range', //'prepositionalAdjunct',
      '1',
      Object.values(obj.references[0])[0], //'reference',
      intransitiveFrameMap[Object.values(obj.references[0])[0]]?.[0] ?? '-', //'domain',
      intransitiveFrameMap[Object.values(obj.references[0])[0]]?.[1] ?? '-', //'range',
    ],
  ]);
  fs.appendFileSync(csvFile, csvRow, 'utf-8');
}

export async function createNewCsvFilesAttributeAdjective(
  csvFile: string,
  obj: lemonObject
) {
  let wiktionaryObj: any = undefined;
  await readAndParse('adj', obj.label);
  try {
    wiktionaryObj = fs.readFileSync(
      '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
    );
  } catch (e) {
    console.log(e);
  }
  const csvRow = convertArrayToCSV([
    [
      obj.sense,
      'adjective',
      '-', //'writtenForm', //todo:
      'AdjectiveAttributiveFrame', //'SyntacticFrame', //todo:
      'PredSynArg', //'copulativeSubject', //todo:
      'AttrSynArg', //'attributiveArg', //todo:
      '1', //todo:
      'owl:Restriction', //'reference', //todo:
      '-', //'owl:onProperty',
      '-', //'owl:hasValue',
      '-', //'domain',
      '-', //'range',
    ],
  ]);
  fs.appendFileSync(csvFile, csvRow, 'utf-8');
}

export async function createNewCsvFilesGradableAdjective(
  csvFile: string,
  obj: lemonObject
) {
  let wiktionaryObj: any = undefined;
  await readAndParse('adj', obj.label);
  try {
    wiktionaryObj = fs.readFileSync(
      '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
    );
  } catch (e) {
    console.log(e);
  }
  const csvRow = convertArrayToCSV([
    [
      obj.sense,
      'adjective',
      '-', //'writtenForm', //todo:
      '-', //'comparative', //todo:
      '-', //'superlative_singular', //todo:
      '-', //'superlative_plural', //todo:
      'AdjectiveSuperlativeFrame', //todo:
      'PredSynArg', //todo:
      '1',
      'oils:CovariantScalar',
      '-', //'oils:boundTo',
      '-',
      'oils:degree',
      '-', //'domain',
      '-', //'range',
      'in', //todo:
    ],
  ]);
  fs.appendFileSync(csvFile, csvRow, 'utf-8');
}

const ndjson = require('ndjson');

/*
function readAndParse(searchWord: string) {
  const readStream = createReadStream(
    '/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json'
  );
  /!*  const writeStream = createWriteStream(
      '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
    );*!/
  /!*  /!*  let result: any = [];
      const rs = fs
        .createReadStream(
          '/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json'
        )
        .pipe(ndjson.parse())
        .on('data', function (obj: any) {
          if (obj['word'] === searchWord) {
            result.push(obj);
          }
        })
        .on('end', () => result);*!/

    const tunnel = new PassThrough();

    tunnel.on('data', (chunk: any) => {
      console.log('bytes:', chunk);
    });

    readStream.pipe(tunnel).pipe(writeStream).;*!/

  const {stringer} = require('stream-json/jsonl/Stringer');
  const {parser} = require('stream-json/jsonl/Parser');
  const {chain} = require('stream-chain');
  const {pick} = require('stream-json/filters/Pick');
  const fs = require('fs');
  const zlib = require('zlib');
  const Pick = require('stream-json/filters/Pick');
  const emit = require('stream-json//utils/emit.js');
  /!*  const stream = fs.createWriteStream(
      '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
    );*!/
  // roundtrips data
  const pipeline = chain([
    fs.createReadStream(
      '/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json'
    ),
    parser(),
    // TODO: DOENST FIND THE RIGHT WORDS
    (data: any) => {
      const check =
        data.value === 'pos.noun' && data.value.word === `word.${searchWord}`;
      if (check) {
        console.log('searchWord:', searchWord, 'found: ', data.value);
        return data.value;
      }
      return '';
    },
    stringer(),
  ]);
  pipeline.on('finished', (data: any) => console.log('data', data));
  /!* const {streamArray} = require('stream-json/streamers/StreamArray');
   const {streamValues} = require('stream-json/streamers/StreamValues');
 
   const {PassThrough} = require('stream');
 
   const passThrough = new PassThrough();
   // our data stream:
   // {total: 123456789, meta: {...}, data: [...]}
   // we are interested in 'data'
 
   const pipeline = chain([
     fs.createReadStream(
       "/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json'"
     ),
     Pick.withParser({filter: /^pos\.noun/}),
     streamValues(),
   ]);
   //  pipeline.on('error', (err: any) => {});
 
   pipeline.on('data', (data: any) => {
     console.log(data);
     fs.createWriteStream(
       '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json',
       data
     );
   });
   pipeline.on('end', () => {});*!/
}
*/

/*pipeline(
  fs.createReadStream(
    '/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json'
  ),
  ndjson.parse(),
  fs.createWriteStream(
    '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
  )
);*/

/*
function readAndParse(pos: string, label: string) {
  const stream: ReadStream = fs
    .createReadStream(
      '/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json'
    )
    .pipe(ndjson.parse())
    .on('data', function (obj: any) {
      // obj is a javascript object
      if (obj['pos'] === pos && obj['word'] === label) {
        const file = fs.createWriteStream(
          '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
        );
        file.write(JSON.stringify(obj));
      }
    });
}
*/

const Chain = require('stream-chain');

const {parser} = require('stream-json');
const {pick} = require('stream-json/filters/Pick');
const {ignore} = require('stream-json/filters/Ignore');
const {streamValues} = require('stream-json/streamers/StreamValues');

const zlib = require('zlib');

/*const pipeline = chain([
  fs.createReadStream(
    '/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json'
  ),
  parser(),
  pick({filter: 'data'}),
  ignore({filter: /\b_meta\b/i}),
  streamValues(),
  // @ts-ignore
  data => {
    const value = data.value;
    // keep data only for the accounting department
    return value['pos'] === pos && obj['word'] === label ? data : null;
  },
]);*/

function readAndParse(pos: string, label: string) {
  // const readStream = fs.createReadStream(
  //   '/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes_processed/kaikki_dot_org-dictionary-German-by-pos-noun.json'
  // );
  //
  // const writeStream = fs.createWriteStream(
  //   '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
  // );
  // const {chain, final} = require('stream-chain');
  /* const pipeline = chain([
     fs.createReadStream(
       '/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json'
     ),
     parser(),
     /!*    pick({filter: 'data'}),
         ignore({filter: /\b_meta\b/i}),
         streamValues(),
         // @ts-ignore
         data => {
           const value = data.value;
           // keep data only for the accounting department
           return value['pos'] === pos && value['word'] === label ? data : null;
         },*!/
   ]);*/

  /* pipeline
     .on('data', (obj: any) => {
       if (obj['pos'] === pos && obj['word'] === label) {
         return obj;
       }
       return null;
     })
     .pipe(
       fs.createWriteStream(
         '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json'
       )
     );*/
  /*const none = final();
  readStream
    .pipe(
      chain([
        [
          (obj: any) =>
            obj['pos'] === pos && obj['word'] === label ? obj : none,
        ],
      ])
    )
    .pipe(writeStream);*/
  switch (pos) {
    case 'noun':
      const noun = fs.readFileSync(
        '/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes_processed/kaikki_dot_org-dictionary-German-by-pos-noun.json',
        'utf-8'
      );

      return JSON.stringify(noun)
        .split('\n')
        .map(obj => JSON.parse(obj));
    case 'adj':
      const adj = fs.readFileSync(
        '/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes_processed/kaikki_dot_org-dictionary-German-by-pos-adj.json',
        'utf-8'
      );

      return JSON.stringify(adj)
        .split('\n')
        .map(obj => JSON.parse(obj))
        .filter(obj => obj['word'] === label);

    case 'verb':
      const verb = fs.readFileSync(
        '/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes_processed/kaikki_dot_org-dictionary-German-by-pos-verb.json',
        'utf-8'
      );

      return JSON.stringify(verb)
        .split('\n')
        .map(obj => JSON.parse(obj));
    default:
      return;
  }
}
