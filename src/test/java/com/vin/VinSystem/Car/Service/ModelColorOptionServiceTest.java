package com.vin.VinSystem.Car.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;
import com.vin.VinSystem.Car.DTO.ModelColorOptionCreateDTO;
import com.vin.VinSystem.Car.DTO.ModelColorOptionResponseDTO;
import com.vin.VinSystem.Car.Entity.CarColor;
import com.vin.VinSystem.Car.Entity.Model;
import com.vin.VinSystem.Car.Entity.ModelColorOption;
import com.vin.VinSystem.Car.Entity.ModelColorOptionId;
import com.vin.VinSystem.Car.Repository.ModelColorOptionRepository;

class ModelColorOptionServiceTest {

    ModelColorOptionRepository repository    = mock(ModelColorOptionRepository.class);
    ModelService               modelService  = mock(ModelService.class);
    CarColorService            colorService  = mock(CarColorService.class);
    ModelColorOptionService    service;

    @BeforeEach
    void setUp() {
        service = new ModelColorOptionService(repository, modelService, colorService);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Model mockModel() {
        Model m = new Model();
        m.setModelId(1L);
        m.setModelName("VF8");
        return m;
    }

    private CarColor mockColor() {
        CarColor c = new CarColor();
        c.setColorId(2L);
        c.setColorName("Đỏ Mận");
        return c;
    }

    private ModelColorOption mockOption() {
        ModelColorOption o = new ModelColorOption();
        o.setId(new ModelColorOptionId(1L, 2L));
        o.setModel(mockModel());
        o.setColor(mockColor());
        o.setExtraPrice(new BigDecimal("20000000"));
        return o;
    }

    private ModelColorOptionCreateDTO validRequest() {
        ModelColorOptionCreateDTO dto = new ModelColorOptionCreateDTO();
        dto.setModelId(1L);
        dto.setColorId(2L);
        dto.setExtraPrice(new BigDecimal("20000000"));
        return dto;
    }

    // ── addOrUpdateOption ─────────────────────────────────────────────────

    @Test
    void addOrUpdate_create_success() {
        when(modelService.getModelById(1L)).thenReturn(mockModel());
        when(colorService.getColorById(2L)).thenReturn(mockColor());
        when(repository.findById(any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(mockOption());

        ModelColorOptionResponseDTO res = service.addOrUpdateOption(validRequest());

        assertThat(res.getModelId()).isEqualTo(1L);
        assertThat(res.getColorId()).isEqualTo(2L);
        assertThat(res.getExtraPrice()).isEqualByComparingTo("20000000");
        verify(repository).save(any());
    }

    @Test
    void addOrUpdate_update_existing() {
        ModelColorOption existing = mockOption();

        when(modelService.getModelById(1L)).thenReturn(mockModel());
        when(colorService.getColorById(2L)).thenReturn(mockColor());
        when(repository.findById(any())).thenReturn(Optional.of(existing));

        ModelColorOption updated = mockOption();
        updated.setExtraPrice(new BigDecimal("30000000"));
        when(repository.save(any())).thenReturn(updated);

        ModelColorOptionCreateDTO dto = validRequest();
        dto.setExtraPrice(new BigDecimal("30000000"));

        ModelColorOptionResponseDTO res = service.addOrUpdateOption(dto);

        assertThat(res.getExtraPrice()).isEqualByComparingTo("30000000");
    }

    @Test
    void addOrUpdate_nullExtraPrice_defaultsToZero() {
        when(modelService.getModelById(1L)).thenReturn(mockModel());
        when(colorService.getColorById(2L)).thenReturn(mockColor());
        when(repository.findById(any())).thenReturn(Optional.empty());

        ModelColorOption saved = mockOption();
        saved.setExtraPrice(BigDecimal.ZERO);
        when(repository.save(any())).thenReturn(saved);

        ModelColorOptionCreateDTO dto = validRequest();
        dto.setExtraPrice(null);

        ModelColorOptionResponseDTO res = service.addOrUpdateOption(dto);

        assertThat(res.getExtraPrice()).isEqualByComparingTo("0");
    }

    // ── getOption ─────────────────────────────────────────────────────────

    @Test
    void getOption_success() {
        when(repository.findById(new ModelColorOptionId(1L, 2L)))
            .thenReturn(Optional.of(mockOption()));

        ModelColorOptionResponseDTO res = service.getOption(1L, 2L);

        assertThat(res.getModelId()).isEqualTo(1L);
        assertThat(res.getColorId()).isEqualTo(2L);
    }

    @Test
    void getOption_notFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOption(1L, 99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("modelId=1");
    }

    // ── getOptionWithDefault ──────────────────────────────────────────────

    @Test
    void getOptionWithDefault_found() {
        when(repository.findById(new ModelColorOptionId(1L, 2L)))
            .thenReturn(Optional.of(mockOption()));

        ModelColorOptionResponseDTO res = service.getOptionWithDefault(1L, 2L);

        assertThat(res.getExtraPrice()).isEqualByComparingTo("20000000");
    }

    @Test
    void getOptionWithDefault_notFound_returnsZero() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        ModelColorOptionResponseDTO res = service.getOptionWithDefault(1L, 99L);

        assertThat(res.getExtraPrice()).isEqualByComparingTo("0");
        assertThat(res.getModelId()).isEqualTo(1L);
        assertThat(res.getColorId()).isEqualTo(99L);
    }

    // ── batchLoadOptions ──────────────────────────────────────────────────

    @Test
    void batchLoad_success() {
        when(repository.findAllByModelColorIds(List.of(1L), List.of(2L)))
            .thenReturn(List.of(mockOption()));

        Map<String, ModelColorOptionResponseDTO> result =
            service.batchLoadOptions(List.of(1L), List.of(2L));

        assertThat(result).containsKey("1_2");
        assertThat(result.get("1_2").getExtraPrice()).isEqualByComparingTo("20000000");
    }

    @Test
    void batchLoad_emptyInput_returnsEmptyMap() {
        assertThat(service.batchLoadOptions(List.of(), List.of(2L))).isEmpty();
        assertThat(service.batchLoadOptions(List.of(1L), List.of())).isEmpty();
        assertThat(service.batchLoadOptions(null, null)).isEmpty();
        verify(repository, never()).findAllByModelColorIds(any(), any());
    }

    // ── deleteOption ──────────────────────────────────────────────────────

    @Test
    void deleteOption_success() {
        service.deleteOption(1L, 2L);

        verify(repository).deleteById(new ModelColorOptionId(1L, 2L));
    }

    // ── ensureOptionExists ────────────────────────────────────────────────

    @Test
    void ensureOptionExists_alreadyExists_noSave() {
        when(repository.existsById(new ModelColorOptionId(1L, 2L))).thenReturn(true);

        service.ensureOptionExists(1L, 2L);

        verify(repository, never()).save(any());
    }

    @Test
    void ensureOptionExists_notExists_creates() {
        when(repository.existsById(new ModelColorOptionId(1L, 2L))).thenReturn(false);
        when(modelService.getModelById(1L)).thenReturn(mockModel());
        when(colorService.getColorById(2L)).thenReturn(mockColor());
        when(repository.save(any())).thenReturn(mockOption());

        service.ensureOptionExists(1L, 2L);

        verify(repository).save(any());
    }
}