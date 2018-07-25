/**
 * Enterprise Spreadsheet Solutions
 * Copyright(c) FeyaSoft Inc. All right reserved.
 * info@enterpriseSheet.com
 * http://www.enterpriseSheet.com
 * 
 * Licensed under the EnterpriseSheet Commercial License.
 * http://enterprisesheet.com/license.jsp
 * 
 * You need to have a valid license key to access this file.
 */
Ext.define('EnterpriseSheet.model.FileModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'author', type: 'string'},
        {name: 'exname', type: 'string'},
        {name: 'name', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'permission', type: 'string'},
        {name: 'updateDate', type: 'string'},
        {name: 'userRole', type: 'string'}
    ]
});