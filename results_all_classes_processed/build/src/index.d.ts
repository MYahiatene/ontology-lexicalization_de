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
