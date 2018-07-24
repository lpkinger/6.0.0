Ext.define('erp.view.oa.custom.CustomGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.customgrid',
	layout : 'fit',
	id: 'grid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    multiselected: new Array(),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    orderby: ' order by fd_detno',
    GridUtil: Ext.create('erp.util.GridUtil'),
    bbar: {xtype: 'erpToolbar'},
	initComponent : function(){
		gridCondition = getUrlParam('gridCondition');//从url解析参数
		gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=");
		if(gridCondition==null||gridCondition==""){
				gridCondition="fd_foid=0";
			}
	    this.getGridColumnsAndStore();
		this.callParent(arguments);
	},
	getGridColumnsAndStore: function(){
		var grid = this;
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
        		caller: 'Form!Custom',
        		condition: gridCondition + grid.orderby
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
            		});
            		grid.data = data;
            		if(res.dbfinds.length > 0){
            			grid.dbfinds = res.dbfinds;
            		}
            		//取数据字典配置
                    grid.getDataDictionaryData('CUSTOMTABLE');
            		
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
		    groupField: grid.groupField
		});
	},
	getDataDictionaryData: function(tablename){
		var me = this,data = this.data;
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
        			if(data.length==0){
        				var v_detno=0;
        				Ext.each(res.datadictionary, function(d, index){
        					var arrayfix = ['ct_id','ct_code','ct_recorder','ct_recorddate','ct_status','ct_statuscode',
        					'ct_caller','ct_sourcekind','ct_auditman','ct_auditdate'];  
        					if(Ext.Array.indexOf(arrayfix,d.column_name,0)>-1){
        						o = new Object();
        						o.fd_table = d.table_name;
            					o.fd_field = d.column_name;
            					o.fd_caption = d.comments;
            					o.fd_captionfan = d.comments;
            					o.fd_captionen = d.comments;
            					o.fd_readonly = true;
            					o.fd_allowblank = true;
            					o.fd_columnwidth = 1;
            					o.fd_dbfind = 'F';
            					o.deploy = true;
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
	            				if(contains(d.column_name,'VARCHAR',true)){
	            					o.fd_fieldlength =Number(d.column_name.substring(d.column_name.indexOf('VARCHAR')+7,d.column_name.lastIndexOf('_')));
	            				}else o.fd_fieldlength=100;
	            				o.fd_detno = ++v_detno;
	            				if(d.column_name=='ct_id'||d.column_name=='ct_caller'){
	            					o.fd_field =d.column_name.toUpperCase();
	            					o.fd_type = 'H';
	            				}
	            				if(d.column_name=='ct_sourcekind'){//单据类型
	            					o.fd_type = 'H';
	            				}
	            				if(d.column_name=='ct_statuscode'){//状态码
	            					o.fd_field ='CT_STATUSCODE';
        							o.fd_defaultvalue='ENTERING';
        							o.fd_type = 'H';
        						}
        						if(d.column_name=='ct_status'){//状态
        							o.fd_field ='CT_STATUS';
        							o.fd_defaultvalue='getLocal(ENTERING)';
        						}
        						if(d.column_name=='ct_recorder'){//录入人
        							o.fd_defaultvalue='session:em_name';
        						}
        						if(d.column_name=='ct_recorddate'){
        							o.fd_defaultvalue='getCurrentDate()';
        						}
        						if(d.column_name=='ct_code'){
        							o.fd_field = d.column_name.toUpperCase();
        							o.fd_readonly = false;
        						}
	            				data.push(o);
        					}
        				});
        			}
            		//取Max(序号)
            		var dets = Ext.Array.pluck(data, 'fd_detno');
            		Ext.Array.sort(dets, function(a, b){
            			return b - a;
            		});
            		var det = dets[0];
            		//data里面包含的字段
            		var sel = Ext.Array.pluck(data, 'fd_field');
        			var o = null;
        			Ext.each(res.datadictionary, function(d, index){
        				//将DataDictionary的数据转化成FormDetail数据
        				if(!Ext.Array.contains(sel, d.ddd_fieldname)){
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
            				if(contains(d.column_name,'varchar',true)){
            					o.fd_fieldlength =Number(d.column_name.substring(d.column_name.indexOf('varchar')+7,d.column_name.lastIndexOf('_')));
            				}else o.fd_fieldlength=100;
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
			if(!d['fd_detno']){
				d.fd_detno=0;
			}
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
				if(k == 'fd_readonly' || k == 'fd_allowblank') {
					a[k] = a[k] ? 'T' : 'F';
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
				if(!Ext.isEmpty(d[key]) && d['deploy'] == false) {
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