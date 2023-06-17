"use strict";
// @ts-ignore
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
const fs = __importStar(require("fs"));
const lodash_1 = require("lodash");
const path_1 = require("path");
const { convertArrayToCSV } = require('convert-array-to-csv');
const csvUtils_1 = require("./csvUtils");
const headers_1 = require("./headers");
const path = '/Users/myahiatene/Desktop/results_all_classes_processed/';
function createNewJsonObject(path, classDir) {
    const jsonFile = (0, path_1.join)(path, classDir, classDir + '.json');
    const jsonObject = JSON.parse(fs.readFileSync(jsonFile, 'utf-8'))['@graph'];
    const resultJsonObject = jsonObject
        .map((obj) => {
        const newJsonObject = (0, lodash_1.cloneDeep)(obj);
        newJsonObject.references = [];
        if (obj['sense']) {
            const objSenses = obj['sense'];
            if (typeof objSenses === 'string') {
                newJsonObject.references.push({
                    [objSenses]: jsonObject.find((object) => object['@id'] === objSenses)['reference'],
                });
            }
            if (Array.isArray(objSenses)) {
                objSenses.forEach(sense => newJsonObject.references.push({
                    [sense]: jsonObject.find((o) => o['@id'] === sense)['reference'],
                }));
            }
        }
        return newJsonObject;
    })
        .filter((obj) => obj.references.length > 0)
        .map((obj) => {
        var _a, _b;
        if (obj.partOfSpeech == posTagAdj) {
            const restrictionReference = Object.values(obj.references[0])[0];
            const foundObj = jsonObject.find((oldObj) => oldObj['@id'] === restrictionReference);
            obj.hasValue = (_a = foundObj === null || foundObj === void 0 ? void 0 : foundObj.hasValue) !== null && _a !== void 0 ? _a : '';
            obj.onProperty = (_b = foundObj === null || foundObj === void 0 ? void 0 : foundObj.onProperty) !== null && _b !== void 0 ? _b : '';
        }
        return obj;
    });
    fs.writeFileSync((0, path_1.join)(path, classDir, 'new_' + classDir + '.json'), JSON.stringify(resultJsonObject));
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
    fs.readdir('/Users/myahiatene/Desktop/results_all_classes_processed/', (err, files) => {
        files
            .filter(file => file.includes('result_'))
            .forEach(classDir => {
            try {
                const jsonObject = createNewJsonObject(path, classDir);
                createNewJsonFiles(jsonObject, (0, path_1.join)(path, classDir, 'new_' + classDir + '.json'), classDir);
            }
            catch (e) {
                console.log(e);
            }
        });
    });
}
const posTagAdj = 'http://www.lexinfo.net/ontology/2.0/lexinfo#/adjective';
const posTagNoun = 'http://www.lexinfo.net/ontology/2.0/lexinfo#/noun';
const posTagVerb = 'http://www.lexinfo.net/ontology/2.0/lexinfo#/verb';
function createNewJsonFiles(jsonObject, pathFile, classDir) {
    let nounArr = [];
    let adjArr = [];
    let verbArr = [];
    const csvFileNoun = path + '/' + classDir + '/new_' + classDir + '_noun' + '.csv';
    const csvFileVerbTransitive = path + '/' + classDir + '/new_' + classDir + '_verb_transitive' + '.csv';
    const csvFileVerbIntransitive = path + '/' + classDir + '/new_' + classDir + '_verb_intransitive' + '.csv';
    const csvFileAdjAttribute = path + '/' + classDir + '/new_' + classDir + '_adj_attribute' + '.csv';
    const csvFileAdjGradable = path + '/' + classDir + '/new_' + classDir + '_adj_gradable' + '.csv';
    const csvRow = convertArrayToCSV([headers_1.nounHeader]);
    fs.writeFileSync(csvFileNoun, csvRow, 'utf-8');
    const csvRowTransVerb = convertArrayToCSV([headers_1.transitiveVerbHeader]);
    fs.writeFileSync(csvFileVerbTransitive, csvRowTransVerb, 'utf-8');
    const csvRowIntransVerb = convertArrayToCSV([headers_1.intransitiveVerbHeader]);
    fs.writeFileSync(csvFileVerbIntransitive, csvRowIntransVerb, 'utf-8');
    const csvRowAttrAdj = convertArrayToCSV([headers_1.attributeAdjHeader]);
    fs.writeFileSync(csvFileAdjAttribute, csvRowAttrAdj, 'utf-8');
    const csvRowGradAdj = convertArrayToCSV([headers_1.gradableAdjHeader]);
    fs.writeFileSync(csvFileAdjGradable, csvRowGradAdj, 'utf-8');
    for (const obj of jsonObject) {
        switch (obj.partOfSpeech) {
            case posTagNoun:
                (0, csvUtils_1.createNewCsvFilesNounPPFrame)(csvFileNoun, obj);
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
    fs.writeFileSync(pathFile.replace('.json', '') + '_noun.json', JSON.stringify(nounArr));
    fs.writeFileSync(pathFile.replace('.json', '') + '_verb.json', JSON.stringify(verbArr));
    fs.writeFileSync(pathFile.replace('.json', '') + '_adj.json', JSON.stringify(adjArr));
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
//# sourceMappingURL=index.js.map