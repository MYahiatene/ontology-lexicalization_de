// @ts-ignore

import * as fs from 'fs';
import {cloneDeep} from 'lodash';
import {join} from 'path';
import util from 'util';

const {convertArrayToCSV} = require('convert-array-to-csv');
import {
  createNewCsvFilesNounPPFrame,
  createNewCsvFilesTransitiveFrame,
  createNewCsvFilesIntransitiveFrame,
  createNewCsvFilesAttributeAdjective,
  createNewCsvFilesGradableAdjective,
} from './csvUtils';
import {
  attributeAdjHeader,
  gradableAdjHeader,
  intransitiveVerbHeader,
  nounHeader,
  transitiveVerbHeader,
} from './headers';

const path = '/Users/myahiatene/Desktop/results_all_classes_processed/';

export interface lemonObject {
  '@id': string;
  '@type': string;
  canonicalForm: string;
  language: string;
  sense: string | object;
  partOfSpeech: string;
  label: string;
  references: referenceObject[];
  reference?: string;
  hasValue?: string;
  onProperty?: string | string[];
}

export interface referenceObject {
  [sense: string]: string;
}

function createNewJsonObject(path: string, classDir: string): lemonObject[] {
  const jsonFile = join(path, classDir, classDir + '.json');
  const jsonObject = JSON.parse(fs.readFileSync(jsonFile, 'utf-8'))['@graph'];
  const resultJsonObject: lemonObject[] = jsonObject
    .map((obj: lemonObject) => {
      const newJsonObject: lemonObject = cloneDeep(obj);
      newJsonObject.references = [];
      if (obj['sense']) {
        const objSenses = obj['sense'];
        if (typeof objSenses === 'string') {
          newJsonObject.references.push({
            [objSenses]: jsonObject.find(
              (object: lemonObject) => object['@id'] === objSenses
            )['reference'],
          });
        }
        if (Array.isArray(objSenses)) {
          objSenses.forEach(sense =>
            newJsonObject.references!.push({
              [sense]: jsonObject.find(
                (o: Partial<lemonObject>) => o['@id'] === sense
              )['reference'],
            })
          );
        }
      }
      return newJsonObject;
    })
    .filter((obj: Partial<lemonObject>) => obj.references!.length > 0)
    .map((obj: lemonObject) => {
      if (obj.partOfSpeech == posTagAdj) {
        const restrictionReference: string = Object.values(
          obj.references[0]
        )[0];
        const foundObj: Partial<lemonObject> = jsonObject.find(
          (oldObj: lemonObject) => oldObj['@id'] === restrictionReference
        );
        obj.hasValue = foundObj?.hasValue ?? '';
        obj.onProperty = foundObj?.onProperty ?? '';
      }
      return obj;
    });
  fs.writeFileSync(
    join(path, classDir, 'new_' + classDir + '.json'),
    JSON.stringify(resultJsonObject)
  );
  return resultJsonObject;
}

/*
const csvPathNoun =
  '/Users/myahiatene/Desktop/results_all_classes_processed/NounPPFrame.csv';
const csvPathVerb =
  '/Users/myahiatene/Desktop/results_all_classes_processed/TransitiveIntransitiveFrame.csv';
const csvPathAdj =
  '/Users/myahiatene/Desktop/results_all_classes_processed/GradableAttributeAdjective.csv';
*/

function writeNewJsonFiles() {
  fs.readdir(
    '/Users/myahiatene/Desktop/results_all_classes_processed/',
    (err, files) => {
      files
        .filter(file => file.includes('result_'))
        .forEach(classDir => {
          try {
            const jsonObject = createNewJsonObject(path, classDir);
            createNewJsonFiles(
              jsonObject,
              join(path, classDir, 'new_' + classDir + '.json'),
              classDir
            );
          } catch (e) {
            console.log(e);
          }
        });
    }
  );
}

const posTagAdj = 'http://www.lexinfo.net/ontology/2.0/lexinfo#/adjective';
const posTagNoun = 'http://www.lexinfo.net/ontology/2.0/lexinfo#/noun';
const posTagVerb = 'http://www.lexinfo.net/ontology/2.0/lexinfo#/verb';

function createNewJsonFiles(
  jsonObject: lemonObject[],
  pathFile: string,
  classDir: string
) {
  let nounArr: lemonObject[] = [];
  let adjArr: lemonObject[] = [];
  let verbArr: lemonObject[] = [];
  const csvFileNoun =
    path + '/' + classDir + '/new_' + classDir + '_noun' + '.csv';
  const csvFileVerbTransitive =
    path + '/' + classDir + '/new_' + classDir + '_verb_transitive' + '.csv';
  const csvFileVerbIntransitive =
    path + '/' + classDir + '/new_' + classDir + '_verb_intransitive' + '.csv';
  const csvFileAdjAttribute =
    path + '/' + classDir + '/new_' + classDir + '_adj_attribute' + '.csv';
  const csvFileAdjGradable =
    path + '/' + classDir + '/new_' + classDir + '_adj_gradable' + '.csv';
  const csvRow = convertArrayToCSV([nounHeader]);
  fs.writeFileSync(csvFileNoun, csvRow, 'utf-8');

  const csvRowTransVerb = convertArrayToCSV([transitiveVerbHeader]);
  fs.writeFileSync(csvFileVerbTransitive, csvRowTransVerb, 'utf-8');

  const csvRowIntransVerb = convertArrayToCSV([intransitiveVerbHeader]);
  fs.writeFileSync(csvFileVerbIntransitive, csvRowIntransVerb, 'utf-8');

  const csvRowAttrAdj = convertArrayToCSV([attributeAdjHeader]);
  fs.writeFileSync(csvFileAdjAttribute, csvRowAttrAdj, 'utf-8');

  const csvRowGradAdj = convertArrayToCSV([gradableAdjHeader]);
  fs.writeFileSync(csvFileAdjGradable, csvRowGradAdj, 'utf-8');

  for (const obj of jsonObject) {
    switch (obj.partOfSpeech) {
      case posTagNoun:
        createNewCsvFilesNounPPFrame(csvFileNoun, obj);
        nounArr.push(obj);
        break;
      case posTagVerb:
        // await createNewCsvFilesTransitiveFrame(csvFileVerbTransitive, obj);

        //await createNewCsvFilesIntransitiveFrame(csvFileVerbIntransitive, obj);
        verbArr.push(obj);
        break;
      case posTagAdj:
        //await createNewCsvFilesAttributeAdjective(csvFileAdjAttribute, obj);

        //await createNewCsvFilesGradableAdjective(csvFileAdjGradable, obj);
        adjArr.push(obj);
        break;
      default:
    }
  }
  fs.writeFileSync(
    pathFile.replace('.json', '') + '_noun.json',
    JSON.stringify(nounArr)
  );
  fs.writeFileSync(
    pathFile.replace('.json', '') + '_verb.json',
    JSON.stringify(verbArr)
  );
  fs.writeFileSync(
    pathFile.replace('.json', '') + '_adj.json',
    JSON.stringify(adjArr)
  );
}

writeNewJsonFiles();

/*
const ndjson = require('ndjson');

function readAndParse() {
  const stream = fs
    .createReadStream(
      '/Users/myahiatene/Desktop/results_all_classes_processed/kaikki.org-dictionary-German.json'
    )
    .pipe(ndjson.parse())
    .on('data', function (obj: any) {
      // obj is a javascript object
      if (obj['pos'] === 'noun' && obj['word'] === 'Tag') {
        fs.writeFileSync(
          '/Users/myahiatene/Desktop/results_all_classes_processed/temp.json',
          JSON.stringify(obj),
          'utf8'
        );
        stream.destroy();
      }
    });
}

readAndParse();
const test = require('/Users/myahiatene/Desktop/results_all_classes_processed/temp.json');
console.log(test);
*/
