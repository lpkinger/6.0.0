/**
 * 
 */
Ext.define('erp.view.fa.gs.DetailAssGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.detailassgrid',
	layout : 'fit',
	id: 'assgrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'ars_id',
        	type: 'number'
        },{
        	name: 'ars_detno',
        	type: 'number',
        	format: '0'
        },{
        	name: 'ars_ardid',
        	type: 'number'
        },{
        	name: 'ars_asstype',
        	type: 'string'
        },{
        	name: 'ars_assid',
        	type: 'number'
        },{
        	name: 'ars_asscode',
        	type: 'string'
        },{
        	name: 'ars_assname',
        	type: 'string'
        }],
        data: [],
        sorters: [{
            property : 'ars_detno',
            direction: 'ASC'
        }]
    }),
    columns: [{
    	xtype: 'numbercolumn',
    	hidden: true,
    	dataIndex: 'ars_id'
    },{
    	text: '序号',
    	dataIndex: 'ars_detno',
    	flex: 0.4,
    	xtype: 'numbercolumn',
    	format: '0'
    },{
    	dataIndex: 'ars_ardid',
    	hidden: true,
    	xtype: 'numbercolumn'
    },{
    	dataIndex: 'ars_asstype',
    	text: '辅助核算类型',
    	flex: 1.2
    },{
    	dataIndex: 'ars_assid',
    	text: '辅助核算id',
    	hidden: true
    },{
    	dataIndex: 'ars_asscode',
    	text: '辅助核算编号',
    	flex: 1.2,
    	editor: {
    		xtype: 'textfield'
    	}
    },{
    	dataIndex: 'ars_assname',
    	text: '辅助核算名称',
    	flex: 3
    }],
    bodyStyle:'background-color:#f1f1f1;',
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1,
        listeners: {
        	beforeedit: function(e){
        		if(e.field == 'ars_asscode'){
        			var column = e.column,
        				grid = this.grid,
        				type = grid.asstype[e.rowIdx],
        				kind;
        			if(type){
        				kind = grid.asskind[type];
        				if(kind){
        					column.setEditor(new erp.view.core.trigger.DbfindTrigger({
          						findConfig: kind.ak_addkind == null ? '' : ('ak_addkind=\'' + kind.ak_addkind + '\'')
        					}));
        					var record = e.record;
		    				var set = grid.getDbfindSet(record.get('ars_asstype'));
		    				grid.dbfinds = set.dbfinds;
		    				column.dbfind = set.dbfind;
        				}
        			}
        		}
        	}
        }
    })],
    GridUtil: Ext.create('erp.util.GridUtil'),
    detno: 'ars_detno',
    keyField: 'ars_id',
    necessaryField: 'ars_asstype',
	initComponent : function(){
		this.callParent(arguments);
	},
	cacheStore: new Object(),//所有数据
	cacheAss: new Object(),//asstype改变时，cacheStore改变
	asstype: new Array(),//核算类型编号
	asskind: new Array(),//核算相关code、name、dbfind、table...
	getMyData: function(id, cal){
		var me = this;
		cal = cal || caller;
		if(!me.cacheStore[id]){
			me.cacheAss[id] = Ext.Array.concate(me.asstype, '#');
			if(id == null || id <= 0){
				var data = new Array();
				for(var i=0;i<me.asstype.length;i++){
					var o = new Object();
					o[me.detno] = i + 1;
					o.ars_type = caller;
					data.push(o);
				}
				me.store.loadData(data);
				if(me.asstype.length > 0){
					me.getAssData(id);
				}
			} else {
				var condition = "ars_type='" + cal + "' AND ars_ardid=" + id;
				Ext.Ajax.request({
		        	url : basePath + 'common/singleGridPanel.action',
		        	params: {
		        		caller: "AccountRegister!DetailAss", 
		        		condition: condition
		        	},
		        	method : 'post',
		        	callback : function(options,success,response){
		        		var res = new Ext.decode(response.responseText);
		        		if(res.exception || res.exceptionInfo){
		        			showError(res.exceptionInfo);
		        			return;
		        		}
		        		if(res.detno){
		        			me.detno = res.detno;
		    			}
		    			if(res.keyField){
		    				me.keyField = res.keyField;
		    			}
		    			if(res.necessaryField){
		    				me.necessaryField = res.necessaryField;
		    			}
		    			var data = res.data ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
		        		var _d = new Array(), as = Ext.getCmp('grid').selModel.lastSelected.get('ca_assname').split('#');
		        		for(var i in as) {
		        			var a = as[i], k = null;
		        			for(var j in data) {
		        				if(a == data[j].ars_asstype) {
		        					k = data[j];
		        				}
		        			}
		        			if(k == null) {
		        				k = new Object();
		     					k[me.detno] = i + 1;
		     					k.ars_type = caller;
		        			}
		        			_d.push(k);
		        		}
		        		me.store.loadData(_d);
		        		if(res.dbfinds.length > 0){
		        			me.dbfinds = res.dbfinds;
		        		}
		        		me.getAssData(id);
		        	}
		        });
			}
		} else {
			me.store.loadData(me.cacheStore[id]);
		}
	},
	getEffectData: function(){
		var grid = this;
		var data = new Array();
		Ext.each(Ext.Object.getKeys(grid.cacheStore), function(key){
			Ext.each(grid.cacheStore[key], function(d){
				if(d[grid.necessaryField] != null && d[grid.necessaryField].toString().trim() != ''){
					d['ars_ardid'] = key;
					d['ars_type'] = caller;
					data.push(d);
				}
			});
		});
		return data;
	},
	getAssData: function(id){
		var me = this;
		Ext.each(this.store.data.items, function(item, index){
			var type = me.asstype[index];
			if(me.asskind[type]){
				item.set('ars_asstype', me.asskind[type].ak_name);
			} else {
				Ext.Ajax.request({
			   		url : basePath + 'common/getFieldsData.action',
			   		async: false,
			   		params: {
			   			caller: 'AssKind',
			   			fields: 'ak_name,ak_table,ak_dbfind,ak_asscode,ak_assname,ak_addkind',
			   			condition: "ak_code='" + type + "'"
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);return;
			   			}
		    			if(localJson.success){
		    				var d = localJson.data;
		    				if(Ext.isEmpty(item.get('ars_asstype')))
		    					item.set('ars_asstype', d.ak_name);
		    				me.asskind[type] = d;
			   			}
			   		}
				});
			}
		});
		var data = new Array();
		Ext.each(me.store.data.items, function(){
			data.push(this.data);
		});
		if(data.length > 0){
			me.cacheStore[id] = data;
		}
	},
	getDbfindSet: function(type) {
		var sets = {
			'供应商往来': {
				dbfind: 'Vendor|ve_code',
				dbfinds: [{dbGridField:'ve_code',field:'ars_asscode'},{dbGridField:'ve_name',field:'ars_assname'}]
			},
			'部门': {
				dbfind: 'Department|dp_code',
				dbfinds: [{dbGridField:'dp_code',field:'ars_asscode'},{dbGridField:'dp_name',field:'ars_assname'}]
			},
			'员工': {
				dbfind: 'Employee!ALL|em_code',
				dbfinds: [{dbGridField:'em_code',field:'ars_asscode'},{dbGridField:'em_name',field:'ars_assname'}]
			},
			'仓库': {
				dbfind: 'WareHouse|wh_code',
				dbfinds: [{dbGridField:'wh_code',field:'ars_asscode'},{dbGridField:'wh_description',field:'ars_assname'}]
			},
			'客户往来': {
				dbfind: 'Customer|cu_code',
				dbfinds: [{dbGridField:'cu_code',field:'ars_asscode'},{dbGridField:'cu_name',field:'ars_assname'}]
			}
		};
		return sets[type] || {
			dbfind: 'AssKindDetail|akd_asscode',
			dbfinds: [{dbGridField:'akd_asscode',field:'ars_asscode'},{dbGridField:'akd_assname',field:'ars_assname'}]
		};
	}
});