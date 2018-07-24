Ext.define('erp.view.pm.mps.MrpReplaceGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.MrpReplaceGrid',
	region: 'south',
	layout : 'fit',
	id: 'grid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    })],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    necessaryFields: new Array(),
    necessaryField:'',
    detno: '',//编号字段
    keyField: '',//主键字段
	mainField: '',//对应主表主键的字段
	dbfinds: [],
	caller: null,
	condition: null,
	initComponent : function(){
		var condition = this.condition;
		if(!condition){
			var urlCondition = this.BaseUtil.getUrlParam('gridCondition');
			urlCondition = urlCondition == null || urlCondition == "null" ? "" : urlCondition;
			gridCondition = (gridCondition == null || gridCondition == "null") ? "" : gridCondition;
			gridCondition = gridCondition + urlCondition;
	    	gridCondition = gridCondition.replace(/IS/g, "=");
			/*if(gridCondition.search(/!/) != -1){
				gridCondition = gridCondition.substring(0, gridCondition.length - 4);
			}*/
			condition = gridCondition;
		}
    	var gridParam = {caller: this.caller || caller, condition: condition};
    	this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
		this.callParent(arguments); 
	},
	getGridColumnsAndStore:function(grid,url,param){
			var me = this;
			grid.setLoading(true);
			Ext.Ajax.request({//拿到grid的columns
	        	url : basePath + url,
	        	params: param,
	        	method : 'post',
	        	callback : function(options,success,response){
	        		grid.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo){
	        			showError(res.exceptionInfo);return;
	        		}
	        		if(res.columns){
	            		var data = [];
	            		if(!res.data || res.data.length == 2){
	            		/* var o=new Object();
	            		 o.mr_prodcode='无数据';
	            		 o.mr_repcode='无数据';
	            		 data.push(o);*/
	            		} else {
	            			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
	            		}
	            		//store
	            		var store =Ext.create('Ext.data.Store', {
	            		    fields: res.fields,
	            		    data: data
	            		});
	            		//view
	            		if(grid.selModel.views == null){
	            			grid.selModel.views = [];
	            		}
	            		if(res.dbfinds&&res.dbfinds.length > 0){
	            			grid.dbfinds = res.dbfinds;
	            		}
	            		Ext.each(res.columns, function(column, y){
	        				me.setLogicType(grid, column);
	        			});
	            		grid.reconfigure(store, res.columns);
	        		} else {
	        			grid.hide();
	        			var form = Ext.ComponentQuery.query('form')[0];
	        			me.updateFormPosition(form);//字段较少时，修改form布局
	        		}
	        	}
	        });
	},
	setLogicType: function(grid, column){
		var logic = column.logic;
		if(logic != null){
			if(logic == 'detno'){
				grid.detno = column.dataIndex;
				column.width = 40;
				column.renderer = function(val, meta) {
			        meta.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
			        return val;
			    };
			} else if(logic == 'keyField'){
				grid.keyField = column.dataIndex;
			} else if(logic == 'mainField'){
				grid.mainField = column.dataIndex;
			}else if(logic == 'orNecessField'){
				if(!grid.orNecessField){
					grid.orNecessField = new Array();
				}
				grid.orNecessField.push(column.dataIndex);
				
				
			}else if(logic == 'necessaryField'){
				grid.necessaryField = column.dataIndex;
				if(!grid.necessaryFields){
					grid.necessaryFields = new Array();
				}
				grid.necessaryFields.push(column.dataIndex);
				if(!column.haveRendered){
					column.renderer = function(val, meta, record, x, y, store, view){
						var c = this.columns[y];
						if(val != null && val.toString().trim() != ''){
							if(c.xtype == 'datecolumn'){
								val = Ext.Date.format(val, 'Y-m-d');
							}
							return val;
						} else {
							if(c.xtype == 'datecolumn'){
								val = '';
							}
							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
				  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
						}
				   };
				}
			} else if(logic == 'groupField'){
				grid.groupField = column.dataIndex;
			}
		}
	},
	setReadOnly: function(bool){
		this.readOnly = bool;
	},
	reconfigure: function(store, columns){
    	var d = this.headerCt;
    	if (this.columns.length <= 1 && columns) {
			d.suspendLayout = true;
			d.removeAll();
			d.add(columns);
		}
		if (store) {
			try{
				this.bindStore(store);
			} catch (e){
				
			}
		} else {
			this.getView().refresh();
		}
		if (columns) {
			d.suspendLayout = false;
			this.forceComponentLayout();
		}
		this.fireEvent("reconfigure", this);
    },
	listeners: {
		afterrender: function(grid){
			var me = this;
			if(Ext.isIE && !Ext.isIE11){
				document.body.attachEvent('onkeydown', function(){
					if(window.event.ctrlKey && window.event.keyCode == 67){//Ctrl + C
						var e = window.event;
						if(e.srcElement) {
							window.clipboardData.setData('text', e.srcElement.innerHTML);
						}
					}
				});
			} else {
				document.body.addEventListener("mouseover", function(e){
					if(Ext.isFF5){
						e = e || window.event;
					}
					window.mouseoverData = e.target.value;
		    	});
				document.body.addEventListener("keydown", function(e){
					if(Ext.isFF5){
						e = e || window.event;
					}
					if(e.ctrlKey && e.keyCode == 67){
						me.copyToClipboard(window.mouseoverData);
					}
					if(e.ctrlKey && e.keyCode == 67){
						me.copyToClipboard(window.mouseoverData);
					}
		    	});
			}
		}
	}
});