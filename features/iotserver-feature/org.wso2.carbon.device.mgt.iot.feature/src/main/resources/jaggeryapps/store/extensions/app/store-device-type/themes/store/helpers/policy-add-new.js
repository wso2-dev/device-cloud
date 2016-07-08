/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

var resources = function (page, meta) {
    return {
        js: ['libs/utils.js','libs/js.cookie.js', 'libs/invoker-lib.js', 'libs/codemirror.js', 'libs/select2.full.min.js',
            'libs/sql.js','libs/handlebars/handlebars-v2.0.0.js','libs/handlebars/utils.js', 'policy-add-new.js'],
        css: ['codemirror.css', 'select2.min.css', 'policy-add.css', 'modal.css']
    };
};
