"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.createNewCsvFilesGradableAdjective = exports.createNewCsvFilesAttributeAdjective = exports.createNewCsvFilesIntransitiveFrame = exports.createNewCsvFilesTransitiveFrame = exports.createNewCsvFilesNounPPFrame = void 0;
const fs = __importStar(require("fs"));
const { PassThrough } = require('stream');
const { createReadStream, createWriteStream } = require('fs');
const { convertArrayToCSV } = require('convert-array-to-csv');
const nounMap_1 = require("./nounMap");
const transitiveFrameMap_1 = require("./transitiveFrameMap");
const intransitiveFrameMap_1 = require("./intransitiveFrameMap");
function createNewCsvFilesNounPPFrame(csvFile, obj) {
    var _a, _b, _c, _d, _e, _f, _g, _h, _j, _k, _l, _m, _o, _p, _q, _r, _s, _t, _u, _v, _w, _x, _y, _z, _0, _1, _2, _3;
    const label = obj.label.charAt(0).toUpperCase() + obj.label.slice(1);
    let wiktionaryObj = readAndParse('noun', label);
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
            (_d = (_c = (_b = (_a = wiktionaryObj === null || wiktionaryObj === void 0 ? void 0 : wiktionaryObj['head_templates']) === null || _a === void 0 ? void 0 : _a.at(0)) === null || _b === void 0 ? void 0 : _b.args) === null || _c === void 0 ? void 0 : _c.g) !== null && _d !== void 0 ? _d : '',
            (_h = (_g = (_f = (_e = wiktionaryObj === null || wiktionaryObj === void 0 ? void 0 : wiktionaryObj.forms) === null || _e === void 0 ? void 0 : _e.filter((form) => ['nominative', 'singular'].every((type) => form['tags'].includes(type)))) === null || _f === void 0 ? void 0 : _f.at(0)) === null || _g === void 0 ? void 0 : _g.form) !== null && _h !== void 0 ? _h : '-',
            (_m = (_l = (_k = (_j = wiktionaryObj === null || wiktionaryObj === void 0 ? void 0 : wiktionaryObj.forms) === null || _j === void 0 ? void 0 : _j.filter((form) => ['nominative', 'plural'].every((type) => form['tags'].includes(type)))) === null || _k === void 0 ? void 0 : _k.at(0)) === null || _l === void 0 ? void 0 : _l.form) !== null && _m !== void 0 ? _m : '-',
            (_r = (_q = (_p = (_o = wiktionaryObj === null || wiktionaryObj === void 0 ? void 0 : wiktionaryObj.forms) === null || _o === void 0 ? void 0 : _o.filter((form) => ['accusative', 'singular'].every((type) => form['tags'].includes(type)))) === null || _p === void 0 ? void 0 : _p.at(0)) === null || _q === void 0 ? void 0 : _q.form) !== null && _r !== void 0 ? _r : '-',
            (_v = (_u = (_t = (_s = wiktionaryObj === null || wiktionaryObj === void 0 ? void 0 : wiktionaryObj.forms) === null || _s === void 0 ? void 0 : _s.filter((form) => ['dative', 'singular'].every((type) => form['tags'].includes(type)))) === null || _t === void 0 ? void 0 : _t.at(0)) === null || _u === void 0 ? void 0 : _u.form) !== null && _v !== void 0 ? _v : '-',
            (_z = (_y = (_x = (_w = wiktionaryObj === null || wiktionaryObj === void 0 ? void 0 : wiktionaryObj.forms) === null || _w === void 0 ? void 0 : _w.filter((form) => ['genetive', 'singular'].every((type) => form['tags'].includes(type)))) === null || _x === void 0 ? void 0 : _x.at(0)) === null || _y === void 0 ? void 0 : _y.form) !== null && _z !== void 0 ? _z : '-',
            'von',
            'NounPPFrame',
            'range',
            'domain',
            '1',
            Object.values(obj.references[0])[0],
            (_1 = (_0 = nounMap_1.nounMap[Object.values(obj.references[0])[0]]) === null || _0 === void 0 ? void 0 : _0[0]) !== null && _1 !== void 0 ? _1 : '-',
            (_3 = (_2 = nounMap_1.nounMap[Object.values(obj.references[0])[0]]) === null || _2 === void 0 ? void 0 : _2[1]) !== null && _3 !== void 0 ? _3 : '-',
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
exports.createNewCsvFilesNounPPFrame = createNewCsvFilesNounPPFrame;
async function createNewCsvFilesTransitiveFrame(csvFile, obj) {
    var _a, _b, _c, _d;
    let wiktionaryObj = undefined;
    await readAndParse('noun', obj.label);
    try {
        wiktionaryObj = fs.readFileSync('/Users/myahiatene/Desktop/results_all_classes_processed/temp.json');
    }
    catch (e) {
        console.log(e);
    }
    const csvRow = convertArrayToCSV([
        [
            obj.sense,
            'verb',
            '-',
            '-',
            '-',
            '-',
            'TransitiveFrame',
            'range',
            'domain',
            '1',
            Object.values(obj.references[0])[0],
            (_b = (_a = transitiveFrameMap_1.transitiveFrameMap[Object.values(obj.references[0])[0]]) === null || _a === void 0 ? void 0 : _a[0]) !== null && _b !== void 0 ? _b : '-',
            (_d = (_c = transitiveFrameMap_1.transitiveFrameMap[Object.values(obj.references[0])[0]]) === null || _c === void 0 ? void 0 : _c[1]) !== null && _d !== void 0 ? _d : '-',
            'von',
        ],
    ]);
    fs.appendFileSync(csvFile, csvRow, 'utf-8');
}
exports.createNewCsvFilesTransitiveFrame = createNewCsvFilesTransitiveFrame;
async function createNewCsvFilesIntransitiveFrame(csvFile, obj) {
    var _a, _b, _c, _d;
    let wiktionaryObj = undefined;
    await readAndParse('verb', obj.label);
    try {
        wiktionaryObj = fs.readFileSync('/Users/myahiatene/Desktop/results_all_classes_processed/temp.json');
    }
    catch (e) {
        console.log(e);
    }
    const csvRow = convertArrayToCSV([
        [
            obj.sense,
            'verb',
            '-',
            '-',
            '-',
            '-',
            'in',
            'IntransitivePPFrame',
            'domain',
            'range',
            '1',
            Object.values(obj.references[0])[0],
            (_b = (_a = intransitiveFrameMap_1.intransitiveFrameMap[Object.values(obj.references[0])[0]]) === null || _a === void 0 ? void 0 : _a[0]) !== null && _b !== void 0 ? _b : '-',
            (_d = (_c = intransitiveFrameMap_1.intransitiveFrameMap[Object.values(obj.references[0])[0]]) === null || _c === void 0 ? void 0 : _c[1]) !== null && _d !== void 0 ? _d : '-',
        ],
    ]);
    fs.appendFileSync(csvFile, csvRow, 'utf-8');
}
exports.createNewCsvFilesIntransitiveFrame = createNewCsvFilesIntransitiveFrame;
async function createNewCsvFilesAttributeAdjective(csvFile, obj) {
    let wiktionaryObj = undefined;
    await readAndParse('adj', obj.label);
    try {
        wiktionaryObj = fs.readFileSync('/Users/myahiatene/Desktop/results_all_classes_processed/temp.json');
    }
    catch (e) {
        console.log(e);
    }
    const csvRow = convertArrayToCSV([
        [
            obj.sense,
            'adjective',
            '-',
            'AdjectiveAttributiveFrame',
            'PredSynArg',
            'AttrSynArg',
            '1',
            'owl:Restriction',
            '-',
            '-',
            '-',
            '-', //'range',
        ],
    ]);
    fs.appendFileSync(csvFile, csvRow, 'utf-8');
}
exports.createNewCsvFilesAttributeAdjective = createNewCsvFilesAttributeAdjective;
async function createNewCsvFilesGradableAdjective(csvFile, obj) {
    let wiktionaryObj = undefined;
    await readAndParse('adj', obj.label);
    try {
        wiktionaryObj = fs.readFileSync('/Users/myahiatene/Desktop/results_all_classes_processed/temp.json');
    }
    catch (e) {
        console.log(e);
    }
    const csvRow = convertArrayToCSV([
        [
            obj.sense,
            'adjective',
            '-',
            '-',
            '-',
            '-',
            'AdjectiveSuperlativeFrame',
            'PredSynArg',
            '1',
            'oils:CovariantScalar',
            '-',
            '-',
            'oils:degree',
            '-',
            '-',
            'in', //todo:
        ],
    ]);
    fs.appendFileSync(csvFile, csvRow, 'utf-8');
}
exports.createNewCsvFilesGradableAdjective = createNewCsvFilesGradableAdjective;
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
const { parser } = require('stream-json');
const { pick } = require('stream-json/filters/Pick');
const { ignore } = require('stream-json/filters/Ignore');
const { streamValues } = require('stream-json/streamers/StreamValues');
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
function readAndParse(pos, label) {
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
            const noun = fs.readFileSync('/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes_processed/kaikki_dot_org-dictionary-German-by-pos-noun.json', 'utf-8');
            return JSON.stringify(noun)
                .split('\n')
                .map(obj => JSON.parse(obj));
        case 'adj':
            const adj = fs.readFileSync('/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes_processed/kaikki_dot_org-dictionary-German-by-pos-adj.json', 'utf-8');
            return JSON.stringify(adj)
                .split('\n')
                .map(obj => JSON.parse(obj))
                .filter(obj => obj['word'] === label);
        case 'verb':
            const verb = fs.readFileSync('/Users/myahiatene/Desktop/Bachelorarbeit/ontology-lexicalization_de/results_all_classes_processed/kaikki_dot_org-dictionary-German-by-pos-verb.json', 'utf-8');
            return JSON.stringify(verb)
                .split('\n')
                .map(obj => JSON.parse(obj));
        default:
            return;
    }
}
//# sourceMappingURL=csvUtils.js.map