Ext.define('erp.view.fa.WageQuery',{ 
	extend: 'Ext.Viewport',
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  xtype: 'form',  
	    	  anchor: '100% 14%',
	    	  layout: 'hbox',
	    	  bodyStyle: 'background: #f1f1f1;',
	    	  fieldDefaults: {
	    		margin: '3 2 3 8',
	    		cls: 'form-field-allowBlank'
	    	  },
	    	  items: [{
	    		  xtype: 'monthdatefield',
	    		  fieldLabel: '期间',
	    		  id: 'wi_time',
	    		  name: 'wi_time',
	    		  queryMode: 'local',
	    		  margin: '3 0 3 8',
	    	  }, {
	    		  xtype: 'textfield',
	    		  readonly: true,
	    		  fieldStyle: 'background: #f1f1f1;',
	    		  id: 'fs_code',
	    		  name: 'fs_code',
	    		  margin: '3 2 3 0'
	    	  }, {
	    		  xtype: 'monthdatefield',
	    		  fieldLabel: '期间',
	    		  id: 'frd_yearmonth',
	    		  name: 'frd_yearmonth'
	    	  }],
	    	  tbar: [{
	    		  xtype: 'erpQueryButton' 
	    	  }, '->', {
	    		  xtype: 'erpPrintButton'
	    	  }, {
	    		  name: 'export',
	    		  text: $I18N.common.button.erpExportButton,
	    		  iconCls: 'x-button-icon-excel',
	    		  cls: 'x-btn-gray'
	    	  },{
	    		  xtype: 'erpCloseButton'
	    	  }]
	      }, {
	    	  xtype: 'gridpanel',  
	    	  anchor: '100% 86%',
	    	  columnLines: true,
	    	  columns: [{
	    		  text: '列1',
	    		  cls: 'x-grid-header-1',
	    		  flex: 1
	    	  },{
	    		  text: '列2',
	    		  cls: 'x-grid-header-1',
	    		  flex: 1
	    	  },{
	    		  text: '列3',
	    		  cls: 'x-grid-header-1',
	    		  flex: 1
	    	  },{
	    		  text: '列4',
	    		  cls: 'x-grid-header-1',
	    		  flex: 1
	    	  },{
	    		  text: '列5',
	    		  cls: 'x-grid-header-1',
	    		  flex: 1
	    	  }],
	    	  store: new Ext.data.Store({
	    		  fields: [],
	    		  data: [{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}]
	    	  })
	      }]
		});
		me.callParent(arguments); 
	}
});