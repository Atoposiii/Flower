<template>
  <div class="flower-manage">
    <div style="margin-bottom: 16px;">
      <el-button size="small" @click="$router.push('/admin/dashboard')"><i class="el-icon-back"></i> 返回</el-button>
    </div>
    <el-card>
      <div slot="header">
        <el-row :gutter="10" type="flex" align="middle">
          <el-col :span="4">
            <el-input v-model="filters.name" placeholder="花卉名称" clearable size="small" @keyup.enter.native="handleSearch"></el-input>
          </el-col>
          <el-col :span="4">
            <el-input v-model="filters.family" placeholder="科属" clearable size="small" @keyup.enter.native="handleSearch"></el-input>
          </el-col>
          <el-col :span="4">
            <el-input v-model="filters.flowerColor" placeholder="花色" clearable size="small" @keyup.enter.native="handleSearch"></el-input>
          </el-col>
          <el-col :span="4">
            <el-input v-model="filters.floweringPeriod" placeholder="花期" clearable size="small" @keyup.enter.native="handleSearch"></el-input>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" size="small" @click="handleSearch">搜索</el-button>
            <el-button size="small" @click="handleReset">重置</el-button>
          </el-col>
          <el-col :span="4" style="text-align:right">
            <el-button type="primary" @click="showDialog('add')">添加花卉</el-button>
          </el-col>
        </el-row>
      </div>
      <el-table :data="flowers" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="name" label="名称" width="120"></el-table-column>
        <el-table-column prop="scientificName" label="学名" width="150"></el-table-column>
        <el-table-column prop="family" label="科" width="100"></el-table-column>
        <el-table-column prop="flowerColor" label="花色" width="80"></el-table-column>
        <el-table-column prop="category" label="类别" width="100"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" @click="showDialog('edit', scope.row)">编辑</el-button>
            <el-button type="text" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @current-change="handlePageChange"
        :current-page="pagination.page"
        :page-size="pagination.size"
        :total="pagination.total"
        layout="total, prev, pager, next">
      </el-pagination>
    </el-card>
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="70%">
      <el-form :model="flowerForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称"><el-input v-model="flowerForm.name"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学名"><el-input v-model="flowerForm.scientificName"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="科"><el-input v-model="flowerForm.family"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="属"><el-input v-model="flowerForm.genus"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="花色"><el-input v-model="flowerForm.flowerColor"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类别"><el-input v-model="flowerForm.category"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="花期"><el-input v-model="flowerForm.floweringPeriod"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="花香"><el-input v-model="flowerForm.flowerFragrance"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="花语"><el-input v-model="flowerForm.flowerLanguage"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="株高"><el-input v-model="flowerForm.plantHeight"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="冠幅"><el-input v-model="flowerForm.crownWidth"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="叶形"><el-input v-model="flowerForm.leafShape"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="叶色"><el-input v-model="flowerForm.leafColor"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="花型"><el-input v-model="flowerForm.flowerType"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="果期"><el-input v-model="flowerForm.fruitPeriod"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原产地"><el-input v-model="flowerForm.origin"></el-input></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="分布区域"><el-input v-model="flowerForm.distribution"></el-input></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="文化寓意"><el-input v-model="flowerForm.culturalMeaning" type="textarea"></el-input></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述"><el-input v-model="flowerForm.description" type="textarea"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="栽培难度"><el-input v-model="flowerForm.cultivationDifficulty"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适宜季节"><el-input v-model="flowerForm.suitableSeason"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="光照要求"><el-input v-model="flowerForm.sunlightRequirement"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="水分要求"><el-input v-model="flowerForm.waterRequirement"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="土壤要求"><el-input v-model="flowerForm.soilRequirement"></el-input></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="温度要求"><el-input v-model="flowerForm.temperatureRequirement"></el-input></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="施肥要点"><el-input v-model="flowerForm.fertilizationTechnique"></el-input></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="修剪要点"><el-input v-model="flowerForm.pruningTechnique"></el-input></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="病虫害防治"><el-input v-model="flowerForm.pestControl"></el-input></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="养护要点"><el-input v-model="flowerForm.maintenanceTips"></el-input></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="封面图片URL"><el-input v-model="flowerForm.coverImage"></el-input></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import adminAPI from '@/api/admin'

export default {
  name: 'AdminFlowers',
  data() {
    return {
      flowers: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '添加花卉',
      isEdit: false,
      flowerId: null,
      flowerForm: {
        name: '',
        scientificName: '',
        family: '',
        genus: '',
        flowerColor: '',
        category: '',
        floweringPeriod: '',
        flowerFragrance: '',
        flowerLanguage: '',
        plantHeight: '',
        crownWidth: '',
        leafShape: '',
        leafColor: '',
        flowerType: '',
        fruitPeriod: '',
        origin: '',
        distribution: '',
        culturalMeaning: '',
        description: '',
        cultivationDifficulty: '',
        suitableSeason: '',
        sunlightRequirement: '',
        waterRequirement: '',
        soilRequirement: '',
        temperatureRequirement: '',
        fertilizationTechnique: '',
        pruningTechnique: '',
        pestControl: '',
        maintenanceTips: '',
        coverImage: ''
      },
      filters: {
        name: '',
        family: '',
        flowerColor: '',
        floweringPeriod: ''
      },
      pagination: {
        page: 1,
        size: 10,
        total: 0
      }
    }
  },
  mounted() {
    this.loadFlowers()
  },
  methods: {
    handleSearch() {
      this.pagination.page = 1
      this.loadFlowers()
    },
    handleReset() {
      this.filters = { name: '', family: '', flowerColor: '', floweringPeriod: '' }
      this.pagination.page = 1
      this.loadFlowers()
    },
    async loadFlowers() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.page - 1,
          size: this.pagination.size
        }
        if (this.filters.name) params.name = this.filters.name
        if (this.filters.family) params.family = this.filters.family
        if (this.filters.flowerColor) params.flowerColor = this.filters.flowerColor
        if (this.filters.floweringPeriod) params.floweringPeriod = this.filters.floweringPeriod
        const res = await adminAPI.flower.getFlowers(params)
        this.flowers = res.content || []
        this.pagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载花卉列表失败')
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadFlowers()
    },
    showDialog(type, row) {
      if (type === 'add') {
        this.dialogTitle = '添加花卉'
        this.isEdit = false
        this.flowerForm = {
          name: '', scientificName: '', family: '', genus: '', flowerColor: '',
          category: '', floweringPeriod: '', flowerFragrance: '', flowerLanguage: '',
          plantHeight: '', crownWidth: '', leafShape: '', leafColor: '', flowerType: '',
          fruitPeriod: '', origin: '', distribution: '', culturalMeaning: '',
          description: '', cultivationDifficulty: '', suitableSeason: '',
          sunlightRequirement: '', waterRequirement: '', soilRequirement: '',
          temperatureRequirement: '', fertilizationTechnique: '', pruningTechnique: '',
          pestControl: '', maintenanceTips: '', coverImage: ''
        }
      } else {
        this.dialogTitle = '编辑花卉'
        this.isEdit = true
        this.flowerId = row.id
        this.flowerForm = { ...row }
      }
      this.dialogVisible = true
    },
    async handleSave() {
      try {
        if (this.isEdit) {
          await adminAPI.flower.updateFlower(this.flowerId, this.flowerForm)
          this.$message.success('更新成功')
        } else {
          await adminAPI.flower.createFlower(this.flowerForm)
          this.$message.success('添加成功')
        }
        this.dialogVisible = false
        this.loadFlowers()
      } catch (error) {
        this.$message.error('保存失败')
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除这个花卉吗?', '提示', { type: 'warning' })
        await adminAPI.flower.deleteFlower(row.id)
        this.$message.success('删除成功')
        this.loadFlowers()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('删除失败')
      }
    }
  }
}
</script>
