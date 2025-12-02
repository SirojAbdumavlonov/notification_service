package uzumtech.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.dto.merchant.MerchantCreateRequestDto;
import uzumtech.notification.dto.merchant.MerchantResponseDto;
import uzumtech.notification.dto.merchant.MerchantUpdateRequestDto;
import uzumtech.notification.entity.Merchant;
import uzumtech.notification.exception.merchant.MerchantNotFoundException;
import uzumtech.notification.mapper.MerchantMapper;
import uzumtech.notification.repository.MerchantRepository;

import java.util.List;
import java.util.stream.Collectors;

// Сервис для работы с мерчантами
// Работает с DTO для безопасности данных
@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;

    // Создать нового мерчанта из DTO
    @Transactional
    public MerchantResponseDto create(MerchantCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("MerchantCreateRequestDto не может быть null");
        }

        // Проверка уникальности login
        if (merchantRepository.existsByLogin(dto.getLogin())) {
            throw new IllegalArgumentException("Merchant с таким login уже существует: " + dto.getLogin());
        }

        // Проверка уникальности email
        if (merchantRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Merchant с таким email уже существует: " + dto.getEmail());
        }

        // Проверка уникальности taxNumber
        if (merchantRepository.existsByTaxNumber(dto.getTaxNumber())) {
            throw new IllegalArgumentException("Merchant с таким taxNumber уже существует: " + dto.getTaxNumber());
        }

        // Создаем entity из DTO через mapper
        Merchant merchant = merchantMapper.toEntity(dto);
        // TODO: добавить BCryptPasswordEncoder для хэширования пароля

        Merchant saved = merchantRepository.save(merchant);
        return merchantMapper.toResponseDto(saved);
    }

    // Получить мерчанта по ID
    public MerchantResponseDto findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }

        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new MerchantNotFoundException(id));

        return merchantMapper.toResponseDto(merchant);
    }

    // Получить мерчанта по login
    public MerchantResponseDto findByLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login не может быть пустым");
        }

        Merchant merchant = merchantRepository.findByLogin(login)
                .orElseThrow(() -> new MerchantNotFoundException("Merchant not found with login: " + login));

        return merchantMapper.toResponseDto(merchant);
    }

    // Получить мерчанта по email
    public MerchantResponseDto findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        Merchant merchant = merchantRepository.findByEmail(email)
                .orElseThrow(() -> new MerchantNotFoundException("Merchant not found with email: " + email));

        return merchantMapper.toResponseDto(merchant);
    }

    // Получить всех мерчантов
    public List<MerchantResponseDto> findAll() {
        return merchantRepository.findAll().stream()
                .map(merchantMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // Обновить мерчанта из DTO
    @Transactional
    public MerchantResponseDto update(Long id, MerchantUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }

        if (dto == null) {
            throw new IllegalArgumentException("MerchantUpdateRequestDto не может быть null");
        }

        // Находим существующего мерчанта
        Merchant existing = merchantRepository.findById(id)
                .orElseThrow(() -> new MerchantNotFoundException(id));

        // Проверка уникальности email (если изменяется)
        if (dto.getEmail() != null && !dto.getEmail().isBlank() &&
            !existing.getEmail().equals(dto.getEmail()) &&
            merchantRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email уже используется: " + dto.getEmail());
        }

        // Проверка уникальности taxNumber (если изменяется)
        if (dto.getTaxNumber() != null && !dto.getTaxNumber().isBlank() &&
            !existing.getTaxNumber().equals(dto.getTaxNumber()) &&
            merchantRepository.existsByTaxNumber(dto.getTaxNumber())) {
            throw new IllegalArgumentException("TaxNumber уже используется: " + dto.getTaxNumber());
        }

        // Обновляем entity из DTO через mapper (только не-null поля)
        merchantMapper.updateEntityFromDto(dto, existing);

        Merchant updated = merchantRepository.save(existing);
        return merchantMapper.toResponseDto(updated);
    }

    // Удалить мерчанта по ID
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }

        // Проверяем, существует ли мерчант
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new MerchantNotFoundException(id));

        merchantRepository.delete(merchant);
    }

    // Проверить существование мерчанта по ID
    public boolean exists(Long id) {
        if (id == null) {
            return false;
        }
        return merchantRepository.existsById(id);
    }
}
