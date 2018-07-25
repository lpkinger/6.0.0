Ext.define('erp.view.common.batchDeal.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpBatchDealFormPanel',
	id: 'dealform', 
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	tbar: [{
		name: 'query',
		id: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
			Ext.getCmp('dealform').onQuery();
    	}
	}, '->',  {
    	xtype: 'erpMakeOccurButton',
    	id: 'erpMakeOccurButton',
    	hidden: true
    },
    {
    	xtype: 'erpSaleOccurButton',
    	id: 'erpSaleOccurButton',
    	hidden: true
    },
    {
    	xtype: 'erpVastAnalyseButton',
    	id: 'erpVastAnalyseButton',
    	hidden: true
    },{
    	xtype: 'erpVastPrintButton',
    	id: 'erpVastPrintButton',
    	hidden: true
    },{
    	xtype: 'erpVastDealButton',
    	id: 'erpVastDealButton',
    	hidden: true
    },'-',{
    	name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var grid = Ext.getCmp('batchDealGridPanel');
    		grid.BaseUtil.exportexcel(grid);
    	}
    },'-',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	initComponent : function(){ 
    	this.getItemsAndButtons();
		this.callParent(arguments);
	},
	getItemsAndButtons: function(){
		var me = this;
		me.FormUtil.getActiveTab().setLoading(true);
		Ext.Ajax.request({//拿到form的items
        	url : basePath + 'common/singleFormItems.action',
        	params: {
        		caller: caller, 
        		condition: ''
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.FormUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		me.fo_keyField = res.fo_keyField;
        		me.tablename = res.tablename;
        		if(res.keyField){
        			me.keyField = res.keyField;
        		}
        		if(res.dealUrl){
        			me.dealUrl = res.dealUrl;
        		}
        		me.fo_detailMainKeyField = res.fo_detailMainKeyField;
        		Ext.each(res.items, function(item){
        			if(screen.width >= 1280){//根据屏幕宽度，调整列显示宽度
        				if(item.columnWidth > 0 && item.columnWidth <= 0.34){
        					item.columnWidth = 0.25;
        				} else if(item.columnWidth > 0.34 && item.columnWidth <= 0.67){
        					item.columnWidth = 0.5;
        				}
        			}
        		});
        		me.add(res.items);
        		//解析buttons字符串，并拼成json格式
        		var buttonString = res.buttons;
        		if(buttonString != null && buttonString != ''){
        			if(contains(buttonString, '#', true)){
        				Ext.each(buttonString.split('#'), function(b, index){
        					if(!Ext.getCmp(b)){
        						var btn = Ext.getCmp('erpVastDealButton');
        						if(btn){
        							btn.ownerCt.insert(2, {
            							xtype: b
            						});
            						Ext.getCmp(b).show();
        						}
        					} else {
        						Ext.getCmp(b).show();
        					}
        				});
        			} else {
        				if(Ext.getCmp(buttonString)){
        					Ext.getCmp(buttonString).show();
        				} else {
        					var btn = Ext.getCmp('erpVastDealButton');//Ext.getCmp(buttonString);
                			if(btn){
                				btn.setText($I18N.common.button[buttonString]);
                				btn.show();
                			}
        				}
        			}
        		}
        	}
        });
	},
	onQuery: function(){
		var grid = Ext.getCmp('batchDealGridPanel');
		grid.multiselected = new Array();
		var form = this;
		var urlcondition=getUrlParam('urlcondition');
		var condition=urlcondition==''?'':urlcondition;
		Ext.each(form.items.items, function(f){
		if(condition==''){
		condition +=f.logic+" like '%"+f.value+"%'";
		}
		condition += ' AND (' + f.logic + "like '%" + f.value + "%')";
		});
		var gridParam = {caller: caller, condition: condition};
		grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
	}
});