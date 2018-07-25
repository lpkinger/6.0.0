Ext.define('erp.view.scm.product.GetUUid.Toolbar', {
    extend: 'Ext.toolbar.Paging',
    alias: 'widget.erpComponentGridToolbar',
    doRefresh:function(){
    	this.moveFirst();
    },
    items: [{}],
    updateInfo : function(){
    	 var page=this.child('#inputItem').getValue();
            var me = this,
            displayItem = me.child('#displayItem'),
            pageData = me.getPageData();
            pageData.fromRecord=(page-1)*pageSize+1;
			pageData.toRecord=page*pageSize > dataCount ? dataCount : page*pageSize;//
			pageData.total=dataCount;
            dataCount, msg;
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
        	   totalCount=dataCount;
        	return {
        		total : totalCount,
        		currentPage : store.currentPage,
        		pageCount: Math.ceil(dataCount / pageSize),
        		fromRecord: ((store.currentPage - 1) * store.pageSize) + 1,
        		toRecord: Math.min(store.currentPage * store.pageSize, totalCount)
        	};
        },
        onPagingKeyDown : function(field, e){
            var me = this,
                k = e.getKey(),
                pageData = me.getPageData(),
                increment = e.shiftKey ? 10 : 1,
                pageNum = 0;

            if (k == e.RETURN) {
                e.stopEvent();
                pageNum = me.readPageFromInput(pageData);
                if (pageNum !== false) {
                    pageNum = Math.min(Math.max(1, pageNum), pageData.pageCount);
                    me.child('#inputItem').setValue(pageNum);
                    if(me.fireEvent('beforechange', me, pageNum) !== false){
                    	page = pageNum;
                    	 Ext.getCmp('uuIdGrid').getGridData(lastSelected.data['id'],page,pageSize);
                    }
                    
                }
            } else if (k == e.HOME || k == e.END) {
                e.stopEvent();
                pageNum = k == e.HOME ? 1 : pageData.pageCount;
                field.setValue(pageNum);
            } else if (k == e.UP || k == e.PAGEUP || k == e.DOWN || k == e.PAGEDOWN) {
                e.stopEvent();
                pageNum = me.readPageFromInput(pageData);
                if (pageNum) {
                    if (k == e.DOWN || k == e.PAGEDOWN) {
                        increment *= -1;
                    }
                    pageNum += increment;
                    if (pageNum >= 1 && pageNum <= pageData.pages) {
                        field.setValue(pageNum);
                    }
                }
            }
            me.updateInfo();
            fn(me,pageNum);
        }, 
        moveFirst : function(){
        	var me = this;
            me.child('#inputItem').setValue(1);
            value=1;
        	page = value;        	
        	Ext.getCmp('uuIdGrid').getGridData(lastSelected.data['id'],page,pageSize);
            me.updateInfo();
        	fn(me,value);
        },
        movePrevious : function(){
            var me = this;
            me.child('#inputItem').setValue(me.child('#inputItem').getValue()-1);
            value=me.child('#inputItem').getValue();
        	page = value;
        	Ext.getCmp('uuIdGrid').getGridData(lastSelected.data['id'],page,pageSize);  
            me.updateInfo();
            fn(me,value);
          
        },
        moveNext : function(){
            var me = this,
            last = me.getPageData().pageCount;
            total=last;    
            me.child('#inputItem').setValue(me.child('#inputItem').getValue()+1);
            value=me.child('#inputItem').getValue();
        	page = value;
        	Ext.getCmp('uuIdGrid').getGridData(lastSelected.data['id'],page,pageSize);
            me.updateInfo();
            fn(me,value);
        },
        moveLast : function(){
            var me = this,
            last = me.getPageData().pageCount;
            total=last;
            me.child('#inputItem').setValue(last);
            value=me.child('#inputItem').getValue();
        	page = value;        	
        	Ext.getCmp('uuIdGrid').getGridData(lastSelected.data['id'],page,pageSize);
            me.updateInfo();
            fn(me,value);
        },
        onLoad : function() {
			var e = this, d, b, c, a;
			if (!e.rendered) {
				return
			}			
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
		afterOnLoad : function() {
			var e = this, d, c, a;
			if (!e.rendered) {
				return
			}
			d = e.getPageData();
			b = d.currentPage;
			c = Math.ceil(dataCount / pageSize);
			a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
			e.child("#afterTextItem").setText(a);
			e.updateInfo();
			e.fireEvent("change", e, d);
		    e.child('#last').setDisabled(c <= 1 || page == c);
		    e.child('#next').setDisabled(c <= 1 || page == c);
		}
});
function fn(me,value){
	me.child('#last').setDisabled(value==total);
    me.child('#next').setDisabled(value==total);
    me.child('#first').setDisabled(value<=1);
    me.child('#prev').setDisabled(value<=1);
}