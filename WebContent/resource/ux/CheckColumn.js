/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * @class Ext.ux.CheckColumn
 * @extends Ext.grid.column.Column
 * <p>A Header subclass which renders a checkbox in each column cell which toggles the truthiness of the associated data field on click.</p>
 * <p><b>Note. As of ExtJS 3.3 this no longer has to be configured as a plugin of the GridPanel.</b></p>
 * <p>Example usage:</p>
 * <pre><code>
// create the grid
var grid = Ext.create('Ext.grid.Panel', {
    ...
    columns: [{
           text: 'Foo',
           ...
        },{
           xtype: 'checkcolumn',
           text: 'Indoor?',
           dataIndex: 'indoor',
           width: 55
        }
    ]
    ...
});
 * </code></pre>
 * In addition to toggling a Boolean value within the record data, this
 * class adds or removes a css class <tt>'x-grid-checked'</tt> on the td
 * based on whether or not it is checked to alter the background image used
 * for a column.
 */
Ext.define('Ext.ux.CheckColumn', {
    extend: 'Ext.grid.column.Column',
    alias: 'widget.checkcolumn',
    constructor: function() {
        this.addEvents(
            /**
             * @event checkchange
             * Fires when the checked state of a row changes
             * @param {Ext.ux.CheckColumn} this
             * @param {Number} rowIndex The row index
             * @param {Boolean} checked True if the box is checked
             */
            'checkchange'
        );
        this.callParent(arguments);
        this.sortable = false;
        if(!this.renderer){
        	this.renderer = this.rendererFn;
        }
        var me = this;
        fn = function(ch){
        	me.selectAll(ch.getAttribute('grid'), ch.getAttribute('cm'), ch.checked);
        };
    },
    headerCheckable: true,
    singleChecked: false,
    listeners: {
    	afterrender: function(){
    		if(this.headerCheckable) {
    			this.setText("<input type='checkbox' id='" + this.dataIndex + "-checkbox' grid='" + 
       				 this.ownerCt.ownerCt.id + "' cm='" + this.dataIndex + "' onclick='fn(this);'/>" + this.text);
    		}
    		if(this.singleChecked) {
    			this.on('checkchange', this.onSingleCheck, this, {delay: 100});
    		}
        }
    },
    /**
     * @private
     * Process and refire events routed from the GridView's processEvent method.
     */
    processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
        if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
        	var record = null;
        	var dataIndex = this.dataIndex;
        	var checked = null;
        	if(view.panel.store.tree){//treegrid
        		var tree = Ext.ComponentQuery.query('treepanel')[0];
        		tree.getRecordByRecordIndex(recordIndex);
        		record = tree.findRecord;
        		checked = !record.get(dataIndex);
        		//如果父节点checked，就把其子孙节点checked,否则unchecked
        		tree.checkRecord(record, dataIndex, checked);
        	} else {//普通的grid
        		record = view.panel.store.getAt(recordIndex);
        		checked = !record.get(dataIndex);
        	}
            record.set(dataIndex, checked);
            this.fireEvent('checkchange', this, recordIndex, checked);
            // cancel selection.
            return false;
        } else {
            return this.callParent(arguments);
        }
    },

    // Note: class names are not placed on the prototype bc renderer scope
    // is not in the header.
    rendererFn : function(value, m, record){
        var cssPrefix = Ext.baseCSSPrefix,
            cls = [cssPrefix + 'grid-checkheader'];

        if (value) {
            cls.push(cssPrefix + 'grid-checkheader-checked');
        }
        return '<div class="' + cls.join(' ') + '">&#160;</div>';
    },
    /**
     * (取消)全选
     */
    selectAll: function(g, c, checked){
    	var grid = Ext.getCmp(g);
    	if(!grid.store)
    		grid = grid.ownerCt;
    	if(grid && grid.store.data){
    		if(checked){
    			grid.store.each(function(){
        			if(!this.get(c)) {
        				this.set(c, true);
        			}
        		});
    		} else {
    			grid.store.each(function(){
        			if(this.get(c)) {
        				this.set(c, false);
        			}
        		});
    		}
    	} else if(grid.store.tree){//tree grid
    		var items = grid.store.tree.root.childNodes;
    		Ext.each(items, function(item){
    			
    		});
    	}
    },
    onSingleCheck: function(cm, rIdx, check) {
    	if(check) {
    		var grid = this.up('grid'), field = this.dataIndex;
        	grid.store.each(function(r, i){
        		if(i != rIdx && r.get(field)) {
        			r.set(field, false);
        		}
        	});
    	}
    }
});

