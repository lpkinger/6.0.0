Ext.define('erp.view.common.datalist.Toolbar', {
	extend: 'Ext.toolbar.Paging',
	alias: 'widget.erpDatalistToolbar',
	doRefresh:function(){
		//this.ownerCt.getColumnsAndStore(caller, null, page, pageSize);
		window.location.reload();
	},
	beforePageText : "第",
  	afterPageText  : "页,共 {0} 页",
  	firstText      : "第一页",
  	prevText       : "上一页",
  	nextText       : "下一页",
  	lastText       : "最后页",
  	refreshText    : "刷新",
  	displayMsg     : "显示 {0} - {1}条，共 {2} 条",
  	emptyMsg       : '没有数据',
	cls: 'u-toolbar',
	items: [{
		margin:'0 0 0 2',
		id: 'erpAddButton',
		name: 'add',
		tooltip: $I18N.common.button.erpAddButton,
		iconCls: 'x-button-icon-detailadd',
		cls: 'x-btn-tb',
		width: 24,
		hidden: true,
		handler: function(btn){
			var g = btn.ownerCt.ownerCt, u = g.BaseUtil; 
			u.onAdd(caller, u.getActiveTab().title, url);
		}
	},{
		margin:'0 0 0 2',
		id:'datalistexport',
		name: 'export',
		tooltip: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
		cls: 'x-btn-tb',
		width: 24,
		hidden:getUrlParam('_noexport')==-1,
		handler : function(i) {
			var me = i.ownerCt;
			me.exportData(me.ownerCt, i);
		}
	},{
		margin:'0 0 0 2',
		itemId: 'close',
		tooltip:$I18N.common.button.erpCloseButton,
		iconCls: 'datalist-close',
		width: 24,
		cls: 'x-btn-tb',
		handler: function(){
			var main = parent.Ext.getCmp("content-panel");
			if(!main){
				var main = parent.parent.Ext.getCmp("content-panel");
			}
			if(main)
				main.getActiveTab().close();
			else if(typeof parentDoc !== 'undefined' && parentDoc) {
				var doc = parent.Ext.getCmp(parentDoc);
				if(doc) {
					doc.fireEvent('close', doc);
				}
			}
		}
	},'-',{
		iconCls: 'x-button-icon-query',
		tooltip: $I18N.common.tip.searchlist,
		width: 24,
		cls: 'x-btn-tb',
		id: 'searchlist'
	},'-',{
		iconCls:'x-button-icon-install',
		tooltip: $I18N.common.tip.customize,
		width: 24,
		cls: 'x-btn-tb',
		id: 'customize'
	},'-',{
		iconCls: 'x-button-icon-detail',
		tooltip: $I18N.common.tip.relativelist,
		cls: 'x-btn-tb',
		width: 24,
		id: 'relativelist',
		handler: function(btn){
			if(relative){
				var url = window.location.href.toString().replace(caller, relative);
				if(url.indexOf('_noc=1')<0){
				if(url.indexOf('?') > -1) {
					url += '&';
				} else {
					url += '?';
				}
				url += '_noc=1';}
				if(url.indexOf('_self')<0 && _self) url+='&_self='+_self;
				//设置岗位权限属性
				if(url.indexOf('_jobemployee')<0 && _jobemployee) url+='&_jobemployee='+_jobemployee;
				window.location.href = url;
			}
		}
	},{
		id:'list_summary',
		xtype:'tbtext'
	}],
	exportData : function(grid, btn, title, customFields) {
		if(!btn.locked) {
			if(dataCount > 6000) {
				btn.setDisabled(true);
				btn.locked = true;
				setTimeout(function(){
					btn.setDisabled(false);
					btn.locked = false;
				}, 8000);
			}
			grid.BaseUtil.createExcel(caller, 'datalist', grid.getCondition(), title, null, customFields);
		}
	},
	updateInfo : function(){
		var page = this.child('#inputItem').getValue();
		var me = this,
			displayItem = me.child('#displayItem'), msg,
			pageData = me.getPageData();
			pageData.fromRecord = (page-1)*pageSize+1;
			dataCount=me.ownerCt.dataCount=dataCount;
			pageData.toRecord = page*pageSize > dataCount ? dataCount : page*pageSize;//
			pageData.total=dataCount;
			if (displayItem) {
				if (dataCount === 0) {
					msg = me.emptyMsg;
				} else {
					msg = Ext.String.format(
							me.displayMsg,
							pageData.fromRecord,
							pageData.toRecord,
							pageData.total
					);
				}
				displayItem.setText(msg);
				me.doComponentLayout();
			}
	},
	getPageData : function(){
		var store = this.store,
			totalCount = store.getTotalCount();
		totalCount=this.ownerCt.dataCount||dataCount;
		return {
			total : totalCount,
			currentPage : page,
			pageCount: Math.ceil(dataCount / pageSize),
			fromRecord: ((store.currentPage - 1) * store.pageSize) + 1,
			toRecord: Math.min(store.currentPage * store.pageSize, totalCount)
		};
	},
	onPagingKeyDown : function(field, e){
		var me = this, k = e.getKey(), grid = me.ownerCt,
			pageData = me.getPageData(),
			increment = e.shiftKey ? 10 : 1, pageNum = 0, s = Ext.EventObject;
		    caller=grid.caller||caller;
		if (k == s.RETURN) {
			e.stopEvent();
			pageNum = me.readPageFromInput(pageData);
			if (pageNum !== false) {
				pageNum = Math.min(Math.max(1, pageNum), pageData.pageCount);
				me.child('#inputItem').setValue(pageNum);
				if(me.fireEvent('beforechange', me, pageNum) !== false){
					page = pageNum;
					grid.getColumnsAndStore(caller, null, page, pageSize);
				}
			}
		} else if (k == s.HOME || k == s.END) {
			e.stopEvent();
			pageNum = k == s.HOME ? 1 : pageData.pageCount;
			field.setValue(pageNum);
		} else if (k == s.UP || k == s.PAGEUP || k == s.DOWN || k == s.PAGEDOWN) {
			e.stopEvent();
			pageNum = me.readPageFromInput(pageData);
			if (pageNum) {
				if (k == s.DOWN || k == s.PAGEDOWN) {
					increment *= -1;
				}
				pageNum += increment;
				if (pageNum >= 1 && pageNum <= pageData.pages) {
					field.setValue(pageNum);
				}
			}
		}
		me.updateInfo();
		fn(me, pageNum);
	}, 
	moveFirst : function(){
		var me = this, grid = me.ownerCt;
		me.child('#inputItem').setValue(1);
		value = 1;
		page = value;
		caller=grid.caller||caller;
		me.ownerCt.getColumnsAndStore(caller, null, page, pageSize);
		me.updateInfo();
		fn(me,value);
	},
	movePrevious : function(){
		var me = this, grid = me.ownerCt;
		me.child('#inputItem').setValue(me.child('#inputItem').getValue()-1);
		value = me.child('#inputItem').getValue();
		page = value;
		caller=grid.caller||caller;
		me.ownerCt.getColumnsAndStore(caller, null, page, pageSize);
		me.updateInfo();
		fn(me,value);
	},
	moveNext : function(){
		var me = this,
			last = me.getPageData().pageCount,
			grid = me.ownerCt;
		total = last;
		me.child('#inputItem').setValue(me.child('#inputItem').getValue()+1);
		value = me.child('#inputItem').getValue();
		page = value;
		caller=grid.caller||caller;
		me.ownerCt.getColumnsAndStore(caller, null, page, pageSize);
		me.updateInfo();
		fn(me,value);
	},
	moveLast : function(){
		var me = this,
		last = me.getPageData().pageCount,
		grid = me.ownerCt;
		total = last;
		me.child('#inputItem').setValue(last);
		value = me.child('#inputItem').getValue();
		page = value;
		caller=grid.caller||caller;
		me.ownerCt.getColumnsAndStore(caller, null, page, pageSize);
		me.updateInfo();
		fn(me,value);
	},
	onLoad : function() {
		var e = this, d, b, c, a;
		if (!e.rendered) {
			return
		}
		dataCount=this.ownerCt.dataCount||dataCount;
		d = e.getPageData();
		b = d.currentPage;
		c = Math.ceil(dataCount / pageSize);
		a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
		e.child("#afterTextItem").setText(a);
		e.child("#inputItem").setValue(b);
		e.child("#first").setDisabled(b === 1);
		e.child("#prev").setDisabled(b === 1);
		e.child("#next").setDisabled(b === c || c===1);//
		e.child("#last").setDisabled(b === c || c===1);
		e.child("#refresh").enable();
		e.updateInfo();
		e.fireEvent("change", e, d);
	},
	onPagingBlur : function(e){
        var inputItem = this.child("#inputItem"),
            curPage;
        dataCount=this.ownerCt.dataCount||dataCount;
        if (inputItem) {
            curPage = this.getPageData().currentPage;
            var e = this, d, b, c, a;
    		d = e.getPageData();
    		b = d.currentPage;
    		c = Math.ceil(dataCount / pageSize);
    		a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
    		e.child("#afterTextItem").setText(a);
    		e.child("#inputItem").setValue(b);
    		e.child("#first").setDisabled(b === 1);
    		e.child("#prev").setDisabled(b === 1);
    		e.child("#next").setDisabled(b === c || c===1);
    		e.child("#last").setDisabled(b === c || c===1);
        }
    },
	afterOnLoad : function(num) {
		var e = this, d, c, a, grid = e.ownerCt;
		if (!e.rendered) {
			return
		}
		d = e.getPageData();
		dataCount=this.ownerCt.dataCount||dataCount;
		b = d.currentPage;
		c = Math.ceil(dataCount / pageSize);
		a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
		e.child("#afterTextItem").setText(a);
		//解决抬头筛选 页码不对
		if(num && num == 1) e.child("#inputItem").setValue(1);
		e.updateInfo();
		e.fireEvent("change", e, d);
		e.child('#last').setDisabled(c <= 1 || page == c);
		e.child('#next').setDisabled(c <= 1 || page == c);
		/**
		 * 反馈编号：2017030224
		 * wsy
		 */
		e.child("#first").setDisabled(page<=1);
		e.child("#prev").setDisabled(page<=1);
		if(grid.noCount) {
			var m = e.down('#more');
			if(!m) {
				m = Ext.create('Ext.panel.Tool', {
					id: 'more',
					type: 'right',
					margin: '0 5 0 5',
					handler: function() {
						grid.getCount(null, null, true);
						m.hide();
						grid.noCount = false;
					}
				});
				e.add(m);
			} else {
				m.show();
			}
		}
	}
});
function fn(me,value){
	me.child('#last').setDisabled(value == total);
	me.child('#next').setDisabled(value == total);
	me.child('#first').setDisabled(value <= 1);
	me.child('#prev').setDisabled(value <= 1);
}