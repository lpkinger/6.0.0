Ext.define('erp.view.oa.flow.flowDefine.formGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.formGrid',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    multiselected: new Array(),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1,
        beforeEdit:function(context){
        	if(context.record.data.fd_isfixed && (context.record.data.fd_isfixed==-1) && (context.field=='fd_table'||context.field=='fd_field'||context.field=='fd_logictype'||context.field=='deploy')) 
        		return false;
        	else return true;
        }
    }), 
    viewConfig: {
        getRowClass: function(record, rowIndex, rowParams, store){
            if (record.data.fd_isfixed && record.data.fd_isfixed==-1 )return 'x-grid-record-gray' ;
        }
    },
    orderby: ' order by fd_detno',
    GridUtil: Ext.create('erp.util.GridUtil'),
    bbar: {
    	xtype: 'erpToolbar',
    	allowExtraButtons: false
    },
	initComponent : function(){
		gridCondition = this.dataId ? ('fd_foid=' + this.dataId) : getUrlParam('gridCondition');
		gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=");
		if(gridCondition == ''){
			this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', 
					{caller: 'Form', condition: gridCondition, _m: 0});
		} else {
			this.getGridColumnsAndStore();
		}
		this.callParent(arguments);
	},
	/**
	 * 根据主表的fo_table，拿到该表的所有字段
	 * 以及配置在formdetail的字段
	 */
	getGridColumnsAndStore: function(){
		var grid = this;
		if(em_type && em_type!='admin'){
    		showError('ERR_POWER_025:您没有修改页面配置的权限!');return;
    	}
		var main = parent.Ext.getCmp("content-panel");
		if(!main)
			main = parent.parent.Ext.getCmp("content-panel");
		if(main){
			main.getActiveTab().setLoading(true);//loading...
		}
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/singleGridPanel.action',
        	async: false,
        	params: {
        		caller: 'Form',
        		condition: gridCondition + grid.orderby,
        		_m: 0
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		if(main){
        			main.getActiveTab().setLoading(false);
        		}
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			grid.columns = res.columns;
        			grid.fields = res.fields;
        			grid.columns.push({
        				xtype: 'checkcolumn',
        				text: '配置',
        				width: 60,
        				dataIndex: 'deploy',
        				cls: "x-grid-header-1",
        				locked: true,
        				editor: {
        					xtype: 'checkbox',
        					cls: "x-grid-checkheader-editor"
        				}
        			});
        			grid.fields.push({name: 'deploy', type: 'bool'});
        			//renderer
        			grid.getRenderer();
            		var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            		Ext.each(data, function(d){
            			d.deploy = true;
            			d.fd_readonly = d.fd_readonly == 'T';
            			d.fd_allowblank = d.fd_allowblank == 'T';
            			d.fd_modify = d.fd_modify == 'T';
            		});
            		grid.data = data;
            		if(res.dbfinds.length > 0){
            			grid.dbfinds = res.dbfinds;
            		}
            		//取数据字典配置
            		if(data[0].fd_table){
            			grid.getDataDictionaryData(data[0].fd_table);
            		}
            		
        		}
        	}
        });
	},
	getRenderer: function(){
		var grid = this;
		Ext.each(grid.columns, function(column, y){
			//logictype
			var logic = column.logic;
			if(logic != null){
				if(logic == 'detno'){
					grid.detno = column.dataIndex;
				} else if(logic == 'keyField'){
					grid.keyField = column.dataIndex;
				} else if(logic == 'mainField'){
					grid.mainField = column.dataIndex;
				} else if(logic == 'necessaryField'){
					grid.necessaryField = column.dataIndex;
					if(!grid.necessaryFields){
						grid.necessaryFields = new Array();
					}
					grid.necessaryFields.push(column.dataIndex);
					if(!column.haveRendered){
						column.renderer = function(val, meta, item){
								if(item.data['fd_id'] != null && item.data['fd_id'] != '' && item.data['fd_id'] != '0' 
		            				&& item.data['fd_id'] != 0) {
									return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png">' +   
										'<span style="color:blue;" title="已配置">' + val + '</span>';
								} else {
									return '<img src="' + basePath + 'resource/images/renderer/important.png">' +  
										'<span style="color:red;" title="未配置">' + val + '</span>';
								}
					   };
					}
				} else if(logic == 'groupField'){
					grid.groupField = column.dataIndex;
				}
			}
		});
	},
	reconfigureGrid: function(){
		var grid = this;
		grid.store = Ext.create('Ext.data.Store', {
		    storeId: 'gridStore',
		    fields: grid.fields,
		    data: grid.data,
		    sorters: [{
				property: 'fd_detno'
		    }]
		});
	},
	getDataDictionaryData: function(tablename){
		var me = this,data = this.data;
		me.dictionary = {};
  		Ext.Ajax.request({
        	url : basePath + 'ma/getDataDictionary.action',
        	async: false,
        	params: {
        		table: tablename
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		} else if(res.success) {
        			me.dictionary[tablename] = res.datadictionary;
            		//取Max(序号)
            		var det = Ext.Array.max(Ext.Array.pluck(data, 'fd_detno'));
            		//data里面包含的字段
            		var sel = [];
            		Ext.Array.each(data, function(d){
            			sel.push(d.fd_field.toLowerCase());
            		});
        			var o = null;
        			Ext.each(res.datadictionary, function(d, index){
        				//将DataDictionary的数据转化成FormDetail数据
        				if(sel.indexOf(d.column_name) == -1){
        					o = new Object();
            				o.fd_table = d.table_name;
            				o.fd_field = d.column_name;
            				o.fd_caption = d.comments;
            				o.fd_captionfan = d.comments;
            				o.fd_captionen = d.comments;
            				o.fd_readonly = false;
            				o.fd_allowblank = true;
            				o.fd_columnwidth = 1;
            				o.fd_dbfind = 'F';
            				o.deploy = false;
            				if(contains(d.data_type, 'VARCHAR2', true)){
            					o.fd_type = 'S';
            				} else if(contains(d.data_type, 'NUMBER', true)){
            					o.fd_type = 'N';
            				} else if(contains(d.data_type,'TIMESTAMP',true)){
            					o.fd_type = 'DT';
            				} else if(d.data_type == 'DATE'){
            					o.fd_type = 'D';
            				} else if(d.data_type == 'NUMBER'){
            					o.fd_type = 'N';
            				} else if(d.data_type == 'FLOAT'){
            					o.fd_type = 'N';
            				} else {
            					o.fd_type = 'S';
            				}
            				o.fd_fieldlength =d.data_length||100;
            				o.fd_detno = ++det;
            				data.push(o);
        				}
        			});
        			me.reconfigureGrid();
        		}
        	}
		});
	},
	getDeleted: function(){
		var grid = this,items = grid.store.data.items,key = grid.keyField,deleted = new Array(),d = null;
		Ext.each(items, function(item){
			d = item.data;
			if(item.dirty && !Ext.isEmpty(d[key]) && d['deploy'] == false) {
				deleted.push(grid.removeKey(d, 'deploy'));
			}
		});
		return deleted;
	},
	getAdded: function(){
		var grid = this,items = grid.store.data.items,key = grid.keyField,added = new Array(),d = null;
		Ext.each(items, function(item){
			d = item.data;
			if(item.dirty && d[key] == 0 && d['deploy'] == true) {
				added.push(grid.removeKey(d, 'deploy'));
			}
		});
		return added;
	},
	getUpdated: function(){
		var grid = this,items = grid.store.data.items,key = grid.keyField,updated = new Array(),d = null;
		Ext.each(items, function(item){
			d = item.data;
			if(item.dirty && !Ext.isEmpty(d[key]) && d[key] != 0 && d['deploy'] == true) {
				updated.push(grid.removeKey(d, 'deploy'));
			}
		});
		return updated;
	},
	removeKey: function(d, key){
		var a = new Object(),keys = Ext.Object.getKeys(d);
		Ext.each(keys, function(k){
			if(k != key) {
				a[k] = d[k];
				if(k == 'fd_readonly' || k == 'fd_allowblank'||k=='fd_modify') {
					a[k] = a[k] ? 'T' : 'F';
				}
				if(k == 'fd_check') {
					a[k] = a[k] ? 1 : 0;
				}
			}
		});
		return a;
	},
	getChange: function(){
		var grid = this,items = grid.store.data.items,key = grid.keyField,
		added = new Array(),updated = new Array(),deleted = new Array(),d = null,e = null;
		Ext.each(items, function(item){
			d = item.data;
			if (item.dirty) {
				e = grid.removeKey(d, 'deploy');
				if(d[key] == 0 && d['deploy'] == true) {
					added.push(e);
				}
				if(!Ext.isEmpty(d[key]) && d[key] != 0 && d['deploy'] == true) {
					updated.push(e);
				}
				if(!Ext.isEmpty(d[key]) && d[key] != 0 && d['deploy'] == false) {
					deleted.push(e);
				}
			}
		});
		return {
			added: added,
			updated: updated,
			deleted: deleted
		};
	}
});