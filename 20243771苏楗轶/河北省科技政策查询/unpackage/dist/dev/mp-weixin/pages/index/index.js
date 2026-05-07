"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const API_BASE = "http://10.39.60.209:8080/api";
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "index",
  setup(__props) {
    const searchForm = common_vendor.ref(new UTSJSONObject({
      name: "",
      document: "",
      organ: "",
      keyword: ""
    }));
    const policyKinds = common_vendor.ref([]);
    const policyList = common_vendor.ref([]);
    const selectedType = common_vendor.ref("");
    const requestApi = (options = null) => {
      return new Promise((resolve, reject) => {
        const url = `${API_BASE}${options.path}`;
        common_vendor.index.__f__("log", "at pages/index/index.uvue:108", `发起API请求: ${url}`);
        common_vendor.index.request(Object.assign(Object.assign({}, options), { url, method: options.method || "GET", header: new UTSJSONObject({
          "Content-Type": "application/json",
          "Accept": "application/json"
        }), timeout: 1e4, success: (res = null) => {
          common_vendor.index.__f__("log", "at pages/index/index.uvue:120", `API请求成功: ${url}`, res);
          if (res.statusCode === 200) {
            resolve(res.data);
          } else {
            common_vendor.index.__f__("error", "at pages/index/index.uvue:124", `API请求失败(${res.statusCode}): ${url}`, res);
            common_vendor.index.showToast({
              title: `请求失败: ${res.statusCode}`,
              icon: "none"
            });
            reject(res);
          }
        }, fail: (err = null) => {
          common_vendor.index.__f__("error", "at pages/index/index.uvue:133", `API请求异常: ${url}`, err);
          common_vendor.index.showToast({
            title: `请求失败: ${err.errMsg ? err.errMsg : "网络错误"}`,
            icon: "none"
          });
          reject(err);
        } }));
      });
    };
    const currentPage = common_vendor.ref(1);
    const pageSize = common_vendor.ref(10);
    const total = common_vendor.ref(0);
    const totalPages = common_vendor.ref(0);
    common_vendor.onMounted(() => {
      loadPolicyKinds();
      loadPolicyList();
    });
    const loadPolicyKinds = () => {
      return common_vendor.__awaiter(this, void 0, void 0, function* () {
        try {
          common_vendor.index.__f__("log", "at pages/index/index.uvue:155", "开始加载政策分类...");
          const res = yield requestApi(new UTSJSONObject({ path: "/policyKinds" }));
          common_vendor.index.__f__("log", "at pages/index/index.uvue:158", "政策分类API响应:", res);
          if (res && res.code === 200) {
            policyKinds.value = res.data || [];
            common_vendor.index.__f__("log", "at pages/index/index.uvue:162", "政策分类数据:", policyKinds.value);
            common_vendor.index.__f__("log", "at pages/index/index.uvue:163", "分类数量:", policyKinds.value.length);
            if (policyKinds.value === null || policyKinds.value === void 0 || policyKinds.value.length === 0) {
              common_vendor.index.__f__("warn", "at pages/index/index.uvue:167", "警告：政策分类列表为空，请检查后端数据");
              common_vendor.index.showToast({
                title: "暂无政策分类数据",
                icon: "none"
              });
            }
          } else {
            common_vendor.index.__f__("error", "at pages/index/index.uvue:174", "获取政策分类失败:", res);
            common_vendor.index.showToast({
              title: res && res.message ? res.message : "获取分类失败",
              icon: "none"
            });
          }
        } catch (error) {
          common_vendor.index.__f__("error", "at pages/index/index.uvue:181", "加载政策分类时发生错误:", error);
          common_vendor.index.showToast({
            title: "加载政策分类失败",
            icon: "none"
          });
        }
      });
    };
    const loadPolicyList = () => {
      return common_vendor.__awaiter(this, void 0, void 0, function* () {
        try {
          const params = new UTSJSONObject({
            pageNum: currentPage.value,
            pageSize: pageSize.value
          });
          if (searchForm.value.name !== null && searchForm.value.name !== void 0 && searchForm.value.name !== "")
            params.name = searchForm.value.name;
          if (searchForm.value.document !== null && searchForm.value.document !== void 0 && searchForm.value.document !== "")
            params.document = searchForm.value.document;
          if (searchForm.value.organ !== null && searchForm.value.organ !== void 0 && searchForm.value.organ !== "")
            params.organ = searchForm.value.organ;
          if (searchForm.value.keyword !== null && searchForm.value.keyword !== void 0 && searchForm.value.keyword !== "")
            params.keyword = searchForm.value.keyword;
          if (selectedType.value !== null && selectedType.value !== void 0 && selectedType.value !== "")
            params.type = selectedType.value;
          common_vendor.index.__f__("log", "at pages/index/index.uvue:202", "请求政策列表，参数:", params);
          let queryString = "";
          Object.keys(params).forEach((key, index) => {
            if (params[key] !== null && params[key] !== void 0 && params[key] !== "") {
              queryString += (index === 0 ? "?" : "&") + `${key}=${encodeURIComponent(params[key])}`;
            }
          });
          common_vendor.index.__f__("log", "at pages/index/index.uvue:211", "请求路径:", `/policies${queryString}`);
          const res = yield requestApi(new UTSJSONObject({
            path: `/policies${queryString}`
          }));
          common_vendor.index.__f__("log", "at pages/index/index.uvue:216", "政策列表API响应:", res);
          if (res && res.code === 200) {
            policyList.value = res.data && res.data.list ? res.data.list : [];
            total.value = res.data && res.data.total ? res.data.total : 0;
            totalPages.value = res.data && res.data.totalPages ? res.data.totalPages : 0;
            common_vendor.index.__f__("log", "at pages/index/index.uvue:223", `加载到政策列表: ${policyList.value.length} 条, 总计: ${total.value} 条`);
            if (policyList.value === null || policyList.value === void 0 || policyList.value.length === 0) {
              common_vendor.index.__f__("info", "at pages/index/index.uvue:227", "提示：当前查询条件下没有找到政策数据");
            }
          } else {
            common_vendor.index.__f__("error", "at pages/index/index.uvue:231", "获取政策列表失败:", res);
            common_vendor.index.showToast({
              title: res && res.message ? res.message : "获取政策列表失败",
              icon: "none"
            });
          }
        } catch (error) {
          common_vendor.index.__f__("error", "at pages/index/index.uvue:238", "加载政策列表时发生错误:", error);
          common_vendor.index.showToast({
            title: "加载政策列表失败",
            icon: "none"
          });
        }
      });
    };
    const handleSearch = () => {
      currentPage.value = 1;
      loadPolicyList();
    };
    const handleReset = () => {
      searchForm.value = {
        name: "",
        document: "",
        organ: "",
        keyword: ""
      };
      selectedType.value = "";
      currentPage.value = 1;
      loadPolicyList();
    };
    const selectPolicyKind = (type) => {
      if (selectedType.value === type) {
        selectedType.value = "";
      } else {
        selectedType.value = type;
      }
      currentPage.value = 1;
      loadPolicyList();
    };
    const viewDetail = (id) => {
      common_vendor.index.navigateTo({
        url: `/pages/detail/detail?id=${id}`
      });
    };
    const goPage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
        loadPolicyList();
      }
    };
    const formatDate = (date = null) => {
      if (date === null || date === void 0 || date === "")
        return "";
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
    };
    return (_ctx, _cache) => {
      "raw js";
      const __returned__ = {
        a: common_assets._imports_0,
        b: searchForm.value.name,
        c: common_vendor.o(($event) => {
          return searchForm.value.name = $event.detail.value;
        }),
        d: searchForm.value.document,
        e: common_vendor.o(($event) => {
          return searchForm.value.document = $event.detail.value;
        }),
        f: searchForm.value.organ,
        g: common_vendor.o(($event) => {
          return searchForm.value.organ = $event.detail.value;
        }),
        h: searchForm.value.keyword,
        i: common_vendor.o(($event) => {
          return searchForm.value.keyword = $event.detail.value;
        }),
        j: common_vendor.o(handleSearch),
        k: common_vendor.o(handleReset),
        l: common_vendor.f(policyKinds.value, (item, k0, i0) => {
          return {
            a: common_vendor.t(item.type),
            b: common_vendor.t(item.policyCount || 0),
            c: item.type,
            d: selectedType.value === item.type ? 1 : "",
            e: common_vendor.o(($event) => {
              return selectPolicyKind(item.type);
            }, item.type)
          };
        }),
        m: common_vendor.f(policyList.value, (policy, k0, i0) => {
          return {
            a: common_vendor.t(policy.name),
            b: common_vendor.t(policy.organ),
            c: common_vendor.t(formatDate(policy.pubdata)),
            d: common_vendor.t(policy.type),
            e: common_vendor.o(($event) => {
              return viewDetail(policy.id);
            }, policy.id),
            f: policy.id
          };
        }),
        n: common_vendor.t(total.value),
        o: currentPage.value <= 1,
        p: common_vendor.o(($event) => {
          return goPage(currentPage.value - 1);
        }),
        q: common_vendor.t(currentPage.value),
        r: currentPage.value >= totalPages.value,
        s: common_vendor.o(($event) => {
          return goPage(currentPage.value + 1);
        }),
        t: common_vendor.sei(common_vendor.gei(_ctx, ""), "view")
      };
      return __returned__;
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
