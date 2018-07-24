Ext.define('Ext.ux.CheckColumn', {
    extend: 'Ext.grid.column.Column',
    alias: 'widget.checkcolumn',
    constructor: function() {
        this.addEvents(
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
    headerCheckable: false,
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
    	if (view.panel.store.getAt(recordIndex).data.ENABLE==0) return ;
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
Ext.define('erp.view.sys.base.SimpleActionGrid',{    
	extend: 'Ext.grid.Panel', 
	columns:[],
	table:'',
	fields:'',
	relfields:'',
	statusfield:'',
	statuscodefield:'',
	saveUrl: '',//保存
	deleteUrl: '',//删除
	updateUrl:'',//修改
	getIdUrl: '',//获得主键值
	codeField:'',//编号字段
	caller:'',
	seq:'',
	alias: 'widget.simpleactiongrid',
	columnLines: true,
	plugins: [
	Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1,
		beforeEdit:function(context){
        	if(context.record.data.ENABLE==0) 
        		return false;
        	else return true;
        }
	})],
	tbar: [{text:'添加',				
			tooltip:'添加',
			iconCls:'btn-add',
			cls: 'simpleactiongrid-addbtn',
			handler:function(btn){
				var grid=this.ownerCt.ownerCt;
				var store = grid.store;
				var o = new Object();
				o.ENABLE=-1;
				store.insert(0,o);
			}
		}
	],
	viewConfig: {
        stripeRows: true,
        enableTextSelection: true//允许选中文字
    },
	frame: true,
	auto:false,
	gridCondition:'1=1',
	initComponent : function(){
		var me=this;
		this.getData(me.auto);
		this.callParent(arguments);
	},
	getData:function(auto){
		if(auto){
			var me=this;
			me.setLoading(true);
	    	Ext.Ajax.request({
		   		url : basePath + 'ma/sysinit/getFieldsDatas.action',
		   		params: {
		   			tablename:me.table,
		   			fields: me.fields,
		   			relfields:me.relfields,
		   			condition: me.gridCondition
		   		},
		   		method : 'post',
		   		callback : function(opt, s, res){
		   			me.setLoading(false);
		   			var r = new Ext.decode(res.responseText);
		   			if(r.exceptionInfo){
		   				showResult('提示',r.exceptionInfo);return;
		   			}
	    			if(r.success){
	    				var data = Ext.decode(r.data);
	    				var store = me.setStore(me, me.fields, data);
	    				me.reconfigure(store);
		   			}
		   		}
			});
		}
	},
	listeners:{
		beforeedit:function(editor, e,eOpts ){
			if(e.record.data.ENABLE=="0"){
				return false;
			}
			return true;
		
		}
	},
	setStore: function(grid, fields, data, groupField, necessaryField){
		fields=fields.split(',');
		var modelName = 'ext-model-' + grid.id;
		Ext.define(modelName, {
			extend: 'Ext.data.Model',
			fields: fields
		});
		var config = {
				model: modelName
		};
		config.data = data;
		var store = Ext.create('Ext.data.Store', config);
		store.each(function(item, x){
			item.index = x;
		});
		if(grid.buffered) {
			var ln = data.length, records = [], i = 0;
			for (; i < ln; i++) {
				records.push(Ext.create(modelName, data[i]));
			}
			store.cacheRecords(records);
		}
		return store;
	},
	//删除前把已审核改为在录入
	beforeDelete:function(grid,id){
		if(grid.statusfield!=''&&grid.statuscodefield!=''){
			Ext.Ajax.request({
				url : basePath + 'ma/sysinit/beforeDelete.action',
				method : 'POST',
					params:{
						status : grid.statusfield,
						statuscode : grid.statuscodefield,
						table : grid.table,
						keyValue : id,
						keyField:grid.keyField
					}
			});
		}		
	},
	removeDetail:function(grid,id){
		grid.setLoading(true);
		grid.beforeDelete(grid,id);
		Ext.Ajax.request({
			url : basePath + grid.deleteUrl,
			params: {
				id: id,
				_noc:1
			},
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showResult('提示',localJson.exceptionInfo);return false;
				}
				if(localJson.success){
					//showResult('提示','删除成功!');
					grid.getData(true);
				} else {
					delFailure();
				}
			}
		});
	},
	adddetail:function(grid,id){
		var record=grid.getSelectionModel().lastSelected;
		var rr=new Object();
		Ext.each(grid.columns,function(c){
			if(c.dataIndex&&c.dataIndex!='ENABLE'){
				if(c.xtype == 'numberfield'){//number类型赋默认值，不然sql无法执行
					if(record.data[c.dataIndex] == null || record.data[c.dataIndex] == ''){
						record.data[c.dataIndex]=0;
					}
				}else if(c.xtype =='checkcolumn'){
					if(record.data[c.dataIndex]){
						record.data[c.dataIndex]=-1;
					}else{
						record.data[c.dataIndex]=0;
					}
				}
				if(c.dataIndex==grid.keyField||c.dataIndex==grid.codeField){//清除主键可编号字段值
					rr[c.dataIndex.toLocaleLowerCase()]='';
				}else{
					rr[c.dataIndex.toLocaleLowerCase()]=record.data[c.dataIndex];
				}			
			}
		});	
		grid.save(rr);
	},
	save:function(param){
		var grid=this;
		//特殊处理
		if(grid.caller=='Warehouse!Base!saas'){//仓库
			if(param['wh_ifdefect']==0){
				param['wh_type']='良品仓';
			}else{
				param['wh_type']='不良品仓';
			}
		}
		var url = grid.saveUrl,saveType='ADD';
		if(grid.codeField){//编号字段处理
			code=param[grid.codeField.toLocaleLowerCase()];
			if(code == null || code == ''){
					Ext.Ajax.request({
				   		url : basePath + 'common/getCodeString.action',
				   		async: false,//同步ajax请求
				   		params: {
				   			caller: grid.caller,//如果table==null，则根据caller去form表取对应table
				   			table: grid.table,
				   			type: 2
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				showResult('提示',localJson.exceptionInfo);
				   			}
			    			if(localJson.success){
			    				param[grid.codeField.toLocaleLowerCase()]=localJson.code;
				   			}
				   		}
					});
				}
			}
			if(grid.keyField){
				kF=param[grid.keyField.toLocaleLowerCase()];
					if(kF== null || kF == ''||kF == 0){
						Ext.Ajax.request({
							url : basePath + grid.getIdUrl,
							method : 'get',
							async: false,
							callback : function(options,success,response){
								var rs = new Ext.decode(response.responseText);
								if(rs.exceptionInfo){
									showResult('提示',rs.exceptionInfo);return;
								}
								if(rs.success){
									param[grid.keyField.toLocaleLowerCase()]=rs.id;
								}
							}
						});
						url = grid.saveUrl;
						saveType='ADD';
					}else {
						url = grid.updateUrl;
						warnMsg='修改成功!';
						saveType='UPDATE';
						keyValue=param[grid.keyField.toLocaleLowerCase()];
					}
			}
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + grid.caller;
		}
		var params=new Object();
		params.formStore= unescape(escape(Ext.JSON.encode(param)));
		params._noc=1;
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params : params,
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showResult('提示','保存成功');
					grid.getData(true);
				} else if(localJson.exceptionInfo){
					showResult('提示',localJson.exceptionInfo);
				} else{
					showResult('提示',"操作失败!");
				}
			}

		});
	},
	DetailUpdateSuccess:function(activeTab,btn){
		activeTab.loadNewStore(activeTab,activeTab.params);
		var win=btn.up('window');
		if(win) win.close();
	}
});