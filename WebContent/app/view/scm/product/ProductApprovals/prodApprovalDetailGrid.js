/**
 * ERP项目gridpanel通用样式2
 */
Ext.define('erp.view.scm.product.ProductApprovals.prodApprovalDetailGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.prodApprovalDetailGrid',
	requires: ['erp.view.core.toolbar.Toolbar'],
	region: 'south',
	layout : 'fit',
	id: 'prodApprovalDetailGrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    loadMask: true,
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    })],
   	features : [Ext.create('Ext.grid.feature.Grouping',{
   		//startCollapsed: true,
        groupHeaderTpl: '{name} (Count:{rows.length})'
    }),{
        ftype : 'summary',
        showSummaryRow : false,//不显示默认合计行
        generateSummaryData: function(){
        	// 避开在grid reconfigure后的计算，节约加载时间50~600ms
	        return {};
        }
    }],
    bbar: {xtype: 'erpToolbar',id:'prodApprovalDetailGridbar'},
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    necessaryField: '',//必填字段
    detno: '',//编号字段
    keyField: '',//主键字段
	mainField: '',//对应主表主键的字段
	dbfinds: [],
	caller: null,
	condition: null,
	gridCondition:null,
	initComponent : function(){
		var urlCondition = this.BaseUtil.getUrlParam('formCondition');
		//定义通过IS拆分后的数值
		var cons=null;
		//存在urlCondition的情况下
		if(urlCondition){
		//对urlCondition进行拆分  urlCondition的格式一半为pp_idIS1
			if(urlCondition.indexOf('IS')>=0){
				cons = urlCondition.split("IS");
			}else{
				cons = urlCondition.split("=");
				if(cons[1].indexOf('\'')>=0){
					cons[1]=cons[1].slice(1,cons[1].length-1);
				}
			}
		}
		var pp_id=0;
		if(cons!=null){
			if(cons[0]&&cons[1]){
				if(cons[0]!=null&&cons[0]!=''){
					if(cons[1]>0){
						pp_id=cons[1];
					}else{
						pp_id=0;
					}
				}
				
			}
		}
		var condition = this.mainField+"='"+pp_id+"'";
    	var gridParam = {caller: this.caller || caller, condition: this.gridCondition||condition, _m: 0};
    	var master = getUrlParam('newMaster');
    	if(master){
    		gridParam.master = master;
    	}
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
    	this.on('summary', this.generateSummaryData, this, {single: true, delay: 1000});
		this.callParent(arguments); 
	},
	getEffectiveData: function(){
		var me = this;
		var effective = new Array();
		var s = this.store.data.items;
		for(var i=0;i<s.length;i++){
			var data = s[i].data;
			if(data[me.keyField] != null && data[me.keyField] != ""){
				effective.push(data);
			}
		}
		return effective;
	},
	setReadOnly: function(bool){
		this.readOnly = bool;
	},
	reconfigure: function(store, columns){
		var me = this,
	        view = me.getView(),
	        originalDeferinitialRefresh,
	        oldStore = me.store,
	        headerCt = me.headerCt,
	        oldColumns = headerCt ? headerCt.items.getRange() : me.columns;
	    if (columns) {
	        columns = Ext.Array.slice(columns);
	    }
	    me.fireEvent('beforereconfigure', me, store, columns, oldStore, oldColumns);
	    if (me.lockable) {
	        me.reconfigureLockable(store, columns);
	    } else {
	        Ext.suspendLayouts();
	        if (columns) {
	            delete me.scrollLeftPos;
	            headerCt.removeAll();
	            headerCt.add(columns);
	        }
	        if (store && (store = Ext.StoreManager.lookup(store)) !== oldStore) {
	            originalDeferinitialRefresh = view.deferInitialRefresh;
	            view.deferInitialRefresh = false;
	            try {
	            	me.bindStore(store);
	            } catch ( e ) {
	            	
	            }
	            view.deferInitialRefresh = originalDeferinitialRefresh;
	        } else {
	            me.getView().refresh();
	        }
	        Ext.resumeLayouts(true);
	    }
	    me.fireEvent('reconfigure', me, store, columns, oldStore, oldColumns);
		this.fireEvent("summary", this);
    },
    generateSummaryData : function() {
		var store = this.store,
        	columns = this.columns, s = this.features[1],
            i = 0, length = columns.length, comp, bar = this.down('erpToolbar');
		if (!bar) return;
        //将feature的data打印在toolbar上面
        for (; i < length; i++ ) {
        	comp = columns[i];
        	if(comp.summaryType) {
                var tb = Ext.getCmp(comp.dataIndex + '_' + comp.summaryType);
                if(!tb){
                	bar.add('-');
                	tb = bar.add({
                		id: comp.dataIndex + '_' + comp.summaryType,
    					itemId: comp.dataIndex,
    					xtype: 'tbtext'
                	});
                }
                var val = s.getSummary(store, comp.summaryType, comp.dataIndex, false);
            	if(comp.xtype == 'numbercolumn') {
            		val = Ext.util.Format.number(val, (comp.format || '0,000.000'));
    			}
            	tb.setText(comp.text + ':' + val);
        	}
        }   	
    },
	/**
	 * Grid上一条
	 */
	prev: function(grid, record){
		grid = grid || Ext.getCmp('grid');
		record = record || grid.selModel.lastSelected;
		if(record){
			//递归查找上一条，并取到数据
			var d = grid.store.getAt(record.index - 1);
			if(d){
				try {
					grid.selModel.select(d);
					return d;
				} catch (e){
					
				}
			} else {
				if(record.index - 1 > 0){
					return this.prev(grid, d);
				} else {
					return null;
				}
			}
		}
	},
	/**
	 * Grid下一条
	 */
	next: function(grid, record){
		grid = grid || Ext.getCmp('grid');
		record = record || grid.selModel.lastSelected;
		if(record){
			//递归查找下一条，并取到数据
			var d = grid.store.getAt(record.index + 1);
			if(d){
				try {
					grid.selModel.select(d);
					return d;
				} catch (e){
					
				}
			} else {
				if(record.index + 1 < grid.store.data.items.length){
					return this.next(grid, d);
				} else {
					return null;
				}
			}
		}
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
	},
	copyToClipboard: function(txt) {
		if(window.clipboardData) { 
			window.clipboardData.clearData(); 
			window.clipboardData.setData('text', txt); 
		} else if (navigator.userAgent.indexOf('Opera') != -1) { 
			window.location = txt; 
		} else if (window.netscape) { 
			try { 
				netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect'); 
			} catch (e) { 
				alert("您的firefox安全限制限制您进行剪贴板操作，请打开'about:config'将signed.applets.codebase_principal_support'设置为true'之后重试"); 
				return false; 
			}
		}
	}
});
