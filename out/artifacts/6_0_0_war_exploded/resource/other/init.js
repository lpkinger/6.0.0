/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
Ext.ns('Ext.samples');

(function() {
	var basePath = document.URL.substring(0, document.URL.lastIndexOf("ERP/")+4);
    Ext.define('Ext.samples.SamplePanel', {
        extend: 'Ext.view.View',
        alias: 'widget.samplepanel',
        autoHeight   : true,
        frame        : false,
        cls          : 'demos',
        itemSelector : 'dl',
        overItemCls  : 'over',
        trackOver    : true,
        tpl          : Ext.create('Ext.XTemplate',
            '<div id="sample-ct">',
                '<tpl for=".">',
                '<div><a name="{id}"></a><h2><div>{title}</div></h2>',
                '<dl>',
                    '<tpl for="items">',
                        '<dd ext:url="' + basePath + 'jsps/common/{url}"><img src="' + basePath + 'resource/images/screens/{icon}"/>',
                            '<div><h4>{text}',
                                '<tpl if="this.isNew(values.status)">',
                                    '<span class="new-sample"> (New)</span>',
                                '</tpl>',
                                '<tpl if="this.isUpdated(values.status)">',
                                    '<span class="updated-sample"> (Updated)</span>',
                                '</tpl>',
                                '<tpl if="this.isExperimental(values.status)">',
                                    '<span class="new-sample"> (Experimental)</span>',
                                '</tpl>',
                            '</h4><p>{desc}</p></div>',
                        '</dd>',
                    '</tpl>',
                '<div style="clear:left"></div></dl></div>',
                '</tpl>',
            '</div>', {
             isExperimental: function(status){
                 return status == 'experimental';
             },
             isNew: function(status){
                 return status == 'new';
             },
             isUpdated: function(status){
                 return status == 'updated';
             }
        }),

        onContainerClick: function(e) {
            var group = e.getTarget('h2', 3, true);

            if (group) {
                group.up('div').toggleCls('collapsed');
            }
        },

        onItemClick : function(record, item, index, e){
            var t = e.getTarget('dd', 5, true);

            if (t && !e.getTarget('a', 2)) {
                var url = t.getAttributeNS('ext', 'url');
                window.open(url);
            }

            return this.callParent(arguments);
        }
    });
})();

Ext.onReady(function() {

    (Ext.defer(function() {
        var catalog = Ext.samples.samplesCatalog;

        for (var i = 0, c; c = catalog[i]; i++) {
            c.id = 'sample-' + i;
        }

        var store = Ext.create('Ext.data.JsonStore', {
            idProperty : 'id',
            fields     : ['id', 'title', 'items'],
            data       : catalog
        });

        Ext.create('Ext.Panel', {
            frame      : false,
            renderTo   : Ext.get('all-demos'),
            height     : 'auto',
            autoScroll : true,
            items      : Ext.create('Ext.samples.SamplePanel', {
                store : store
            })
        });
        Ext.select('#sample-spacer').remove();
    },500));
});


